package domain.updateService

import domain.analysis.mappers.toSeries
import domain.analysis.model.BollingerBandsData
import domain.analysis.transformations.BollingerBands
import domain.common.TAX_MULTIPLIER
import domain.common.getFutureSharePrice
import domain.common.percentBetweenDoubles
import domain.math.MathUtil
import domain.tinkoff.model.TinkoffCandle
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator
import domain.updateService.model.*
import domain.updateService.updates.IndicatorUpdateData
import domain.updateService.updates.telegramUpdates.TelegramIndicatorUpdate
import domain.updateService.updates.telegramUpdates.TelegramSharePriceInsufficientUpdate
import domain.updateService.updates.telegramUpdates.TelegramShareUpdate
import domain.updateService.updates.telegramUpdates.TelegramUpdate
import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository
import domain.utils.DateUtil
import domain.utils.FuturesUtil
import domain.utils.TimeUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UpdateService(
    private val database: DatabaseRepository,
    private val tinkoff: TinkoffRepository
) {
    private val _updates = MutableSharedFlow<TelegramUpdate>()
    val updates = _updates.asSharedFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        run()
    }

    private fun run() = scope.launch {
        while (isActive) {
            Random.nextFloat() * TimeUtil.MINUTE_MILLIS * 2
//            delay(TimeUtil.MINUTE_MILLIS + delayTime.toLong())
            delay(2000)
            checkForUpdates()
            delayNonWorkingHours(9, 50, 18, 49)
        }
    }

    private val weekends = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    @OptIn(ExperimentalTime::class)
    private suspend fun delayNonWorkingHours(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        var now = Clock.System.now()
        var currentDatetime = now.toLocalDateTime(DateUtil.timezoneMoscow)

        if (currentDatetime.dayOfWeek in weekends) {
            val delay = DateUtil.msUntilStartOfDay(now, DayOfWeek.MONDAY)
            delay(delay)
            now = Clock.System.now()
            currentDatetime = now.toLocalDateTime(DateUtil.timezoneMoscow)
        }

        if (DateUtil.isHoursInRange(
                currentDatetime.hour, currentDatetime.minute,
                startHour, startMinute,
                endHour, endMinute
            )
        ) {
            return
        }

        val msUntilWork = DateUtil.msUntilTime(
            now,
            startHour, startMinute
        )

        delay(msUntilWork)
    }


    private suspend fun checkForUpdates() = supervisorScope {
        val usersWithFollowedShares = database.getUsersWithShares()

        val cache = constructCache(usersWithFollowedShares) ?: return@supervisorScope

        usersWithFollowedShares.forEach { user ->
            handleUserFutures(user = user, cache = cache)
        }

        usersWithFollowedShares.forEach {
            handleIndicatorsForUser(user = it, cache = cache)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleUserFutures(
        user: UserWithFollowedShares,
        cache: Cache
    ) {
        val handled = mutableListOf<UserShare>()
        logger.info("User id: ${user.id}")
        for (share in user.shares) {
            val sharePrice = cache.shares[share.ticker] ?: continue
            val futures = cache.sharesToFutures[share.ticker]
            if (futures.isNullOrEmpty()) continue

            val futuresToNotify = mutableListOf<NotifyFuture>()
            for (future in futures) {
                val futurePrice = cache.futures[future.ticker] ?: continue
                val futureSlotPrice = getFutureSharePrice(sharePrice.price, futurePrice.price)
                val percent = percentBetweenDoubles(futureSlotPrice, sharePrice.price)
                val annualPercent = FuturesUtil.getFutureAnnualPercent(percent, future.expirationDate)
                if (abs(annualPercent) >= share.percent) {
                    futuresToNotify.add(
                        NotifyFuture(
                            ticker = future.ticker,
                            name = future.name,
                            price = futurePrice.price,
                            actualDifference = percent,
                            annualPercent = annualPercent,
                            annualAfterTaxes = annualPercent * TAX_MULTIPLIER,
                            expirationDate = future.expirationDate
                        )
                    )
                }
            }

            val shouldNotify = futuresToNotify.isNotEmpty()
            if (share.futuresNotified == shouldNotify) continue
            handled.add(share.copy(futuresNotified = shouldNotify))
            val notifyShare = NotifyShare(
                shareTicker = share.ticker,
                sharePrice = sharePrice.price,
                minimalDifference = share.percent,
                futures = futuresToNotify
            )
            val update = if (shouldNotify)
                TelegramShareUpdate(userId = user.id, share = notifyShare)
            else
                TelegramSharePriceInsufficientUpdate(userId = user.id, share = notifyShare)
            _updates.emit(update)
        }
        logger.info("Handled ${handled.size} shares for user ${user.id}")
        database.updateUserShares(handled)
    }

    private suspend fun constructCache(usersWithFollowedShares: List<UserWithFollowedShares>): Cache? {
        if (usersWithFollowedShares.isEmpty()) return null

        val shares = extractUniqueShares(usersWithFollowedShares)
        val sharesPricesCache = HashMap<String, SecurityPrice>(shares.size)
        val hourlyRsiCache = HashMap<String, Double>(shares.size)
        val dailyRsiCache = HashMap<String, Double>(shares.size)
        val hourlyBollingerBands = HashMap<String, BollingerBandsData>(shares.size)
        val dailyBollingerBands = HashMap<String, BollingerBandsData>(shares.size)

        val sharesTickers = shares.map { it.ticker }

        val sharesToFutures = sharesTickers.associateBy({ it }, {
            val temp = TinkoffShare(ticker = it)
            val resource = tinkoff.getSecurityFutures(temp)
            resource
                .data
                ?.sortedWith(TinkoffFutureComparator)
//                ?.take(4)
                ?: emptyList()
        })

        val futuresList = sharesToFutures.flatMap { it.value }

        for (share in shares) {
            val orderBookResource = tinkoff.getOrderBook(share.uid)
            if (orderBookResource.data == null) {
                logger.info("Unable to get order book for ${share.ticker} because of ${orderBookResource.message}")
                continue
            }

            val orderBook = orderBookResource.data
            if (orderBook.asks.isEmpty()) {
                logger.info("Order book asks for ${share.ticker} is empty")
                continue
            }

            val price = orderBook.asks.first().price
            if (price > 0.0) {
                sharesPricesCache[share.ticker] = SecurityPrice(price = price, lot = share.lot)
            } else {
                continue
            }

            delay(10)

            val dailyCandlesResource = tinkoff.getDailyCandles(share.uid)
            if (dailyCandlesResource.data.isNullOrEmpty()) {
                logger.info("Unable to get daily candles for ${share.ticker} because of ${dailyCandlesResource.message}")
                continue
            }

            val dailyCandles = dailyCandlesResource.data
            val dailyPrices = extractPrices(dailyCandles)

            val hourlyCandlesResource = tinkoff.getHourlyCandles(share.uid)
            if (hourlyCandlesResource.data.isNullOrEmpty()) {
                logger.info("Unable to get hourly candles for share ${share.ticker} because of ${hourlyCandlesResource.message}")
                continue
            }
            val hourlyCandles = hourlyCandlesResource.data
            val hourlyPrices = extractPrices(hourlyCandles)

            dailyRsiCache[share.ticker] = MathUtil.calculateRsi(dailyPrices)
            hourlyRsiCache[share.ticker] = MathUtil.calculateRsi(hourlyPrices)

            val dailySeries = dailyCandles.toSeries()
            val hourlySeries = hourlyCandles.toSeries()

            dailyBollingerBands[share.ticker] = BollingerBands.calculate(dailySeries)
            hourlyBollingerBands[share.ticker] = BollingerBands.calculate(hourlySeries)

            delay(10)
        }

        delay(TimeUtil.SECOND_MILLIS)

        val futuresPrice = mutableMapOf<String, SecurityPrice>()
        for (future in futuresList) {
            val orderBookResource = tinkoff.getOrderBook(future.uid)
            if (orderBookResource.data == null) {
                logger.info("Unable to get order book for future ${future.ticker} because of ${orderBookResource.message}")
                continue
            }

            val orderBook = orderBookResource.data
            if (orderBook.bids.isEmpty()) {
                logger.info("Order book bids for ${future.ticker} is empty")
                futuresPrice[future.ticker] = SecurityPrice(price = orderBook.lastPrice, future.lot)
                continue
            }

            val price = orderBook.bids.first().price
            if (price > 0.0) futuresPrice[future.ticker] = SecurityPrice(price = price, lot = future.lot)
            delay(10)
        }

        return Cache(
            shares = sharesPricesCache,
            sharesToFutures = sharesToFutures,
            futures = futuresPrice,
            hourlyRsi = hourlyRsiCache,
            dailyRsi = dailyRsiCache,
            hourlyBollingerBands = hourlyBollingerBands,
            dailyBollingerBands = dailyBollingerBands
        )
    }


    private suspend fun handleIndicatorsForUser(user: UserWithFollowedShares, cache: Cache) {
        val handled = mutableMapOf<String, UserShare>()

        for (share in user.shares) {
            val price = cache.shares[share.ticker] ?: continue
            val updateData = mutableListOf<IndicatorUpdateData>()

            val rsiData = handleRsiIndicator(share, cache)
            val shouldNotifyRsi = rsiData != null
            //TODO: Make more flexible system
            if (share.rsiNotificationsEnabled && shouldNotifyRsi != share.rsiNotified) {
                val handledShare = share.copy(rsiNotified = shouldNotifyRsi)
                handled[share.ticker] = handledShare

                rsiData?.let { updateData.add(it) }
            }

            val bollingerBandsData = handleBollingerBandsIndicator(share, cache)
            val shouldNotifyBB = bollingerBandsData != null
            if (share.bbNotificationsEnabled && shouldNotifyBB != share.bollingerBandsNotified) {
                val oldShare = handled[share.ticker] ?: share
                handled[share.ticker] = oldShare.copy(bollingerBandsNotified = shouldNotifyBB)
                bollingerBandsData?.let { updateData.add(it) }
            }

            if (updateData.isNotEmpty()) {
                val update = TelegramIndicatorUpdate(
                    userId = user.id,
                    ticker = share.ticker,
                    price = price.price,
                    data = updateData
                )
                _updates.emit(update)
            }
        }
        database.updateUserShares(handled.values.toList())
    }

    private fun handleRsiIndicator(share: UserShare, cache: Cache): IndicatorUpdateData? {
        val dailyRsi = cache.dailyRsi[share.ticker] ?: return null
        val hourlyRsi = cache.hourlyRsi[share.ticker] ?: return null

        if (dailyRsi > MathUtil.RSI_HIGH && hourlyRsi > MathUtil.RSI_HIGH) {
            return IndicatorUpdateData.RsiHighData(
                hourlyRsi = hourlyRsi,
                dailyRsi = dailyRsi
            )
        } else if (dailyRsi < MathUtil.RSI_LOW && hourlyRsi < MathUtil.RSI_LOW) {
            return IndicatorUpdateData.RsiLowData(
                hourlyRsi = hourlyRsi,
                dailyRsi = dailyRsi
            )
        }
        return null
    }

    private fun handleBollingerBandsIndicator(share: UserShare, cache: Cache): IndicatorUpdateData? {
        val hourlyBb = cache.hourlyBollingerBands[share.ticker] ?: return null
        val dailyBb = cache.dailyBollingerBands[share.ticker] ?: return null
        val price = cache.shares[share.ticker]?.price ?: return null

        if (dailyBb.lower > price && hourlyBb.lower > price) {
            return IndicatorUpdateData.BbAboveData(
                hourlyBb = hourlyBb,
                dailyBb = dailyBb
            )
        } else if (dailyBb.upper < price && hourlyBb.upper < price) {
            return IndicatorUpdateData.BbBelowData(
                hourlyBb = hourlyBb,
                dailyBb = dailyBb
            )
        }

        return null
    }

    private fun extractPrices(candles: List<TinkoffCandle>): DoubleArray {
        val res = DoubleArray(candles.size)
        candles.forEachIndexed { index, candle ->
            res[index] = candle.close
        }
        return res
    }

    private fun extractUniqueShares(users: List<UserWithFollowedShares>): List<TinkoffShare> {
        val tickers = mutableSetOf<String>()
        for (user in users) {
            for (share in user.shares) {
                tickers.add(share.ticker)
            }
        }

        return tickers.mapNotNull {
            tinkoff.getSecurity(it).data
        }
    }
}