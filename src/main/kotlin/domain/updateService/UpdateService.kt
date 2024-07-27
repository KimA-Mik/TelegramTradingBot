package domain.updateService

import Resource
import domain.common.TAX_MULTIPLIER
import domain.common.getFutureSharePrice
import domain.common.percentBetweenDoubles
import domain.math.MathUtil
import domain.tinkoff.model.TinkoffCandle
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator
import domain.updateService.model.IndicatorCache
import domain.updateService.model.NotifyFuture
import domain.updateService.model.NotifyShare
import domain.updateService.model.UserWithFollowedShares
import domain.updateService.updates.IndicatorUpdateData
import domain.updateService.updates.agentUpdates.AgentIndicatorUpdate
import domain.updateService.updates.agentUpdates.AgentSharePriceInsufficientUpdate
import domain.updateService.updates.agentUpdates.AgentShareUpdate
import domain.updateService.updates.agentUpdates.AgentUpdate
import domain.updateService.updates.telegramUpdates.TelegramIndicatorUpdate
import domain.updateService.updates.telegramUpdates.TelegramSharePriceInsufficientUpdate
import domain.updateService.updates.telegramUpdates.TelegramShareUpdate
import domain.updateService.updates.telegramUpdates.TelegramUpdate
import domain.user.model.UserShare
import domain.user.repository.DatabaseRepository
import domain.utils.DateUtil
import domain.utils.FuturesUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.random.Random

class UpdateService(
    private val database: DatabaseRepository,
    private val tinkoff: TinkoffRepository
) {
    private val _updates = MutableSharedFlow<TelegramUpdate>()
    val updates = _updates.asSharedFlow()

    private val _agentUpdates = MutableSharedFlow<AgentUpdate>()
    val agentUpdates = _agentUpdates.asSharedFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        run()
    }

    private fun run() = scope.launch {
        while (isActive) {
            val delayTime = Random.nextFloat() * MILLIS_MINUTE * 2
            delay(MILLIS_MINUTE + delayTime.toLong())
            checkForUpdates()
            delayNonWorkingHours(9, 50, 18, 49)
        }
    }

    private val weekends = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
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
        if (usersWithFollowedShares.isEmpty()) return@supervisorScope

        val set = mutableSetOf<UserShare>()
        usersWithFollowedShares.forEach { userWithFollowedShares ->
            userWithFollowedShares.shares.forEach { share ->
                set.add(share)
            }
        }

        val shares = set.toList()
        val sharesTickers = shares.map { it.ticker }

        val sharesPricesDeferred = async {
            getSharesPrices(sharesTickers)
        }

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
        val futuresPricesDeferred = async {
            getFuturesPrices(futuresList)
        }

        val sharesPrices = when (val res = sharesPricesDeferred.await()) {
            is Resource.Success -> res.data!!.associateBy { it.uid }
            is Resource.Error -> {
                logger.info("Unable to load shares prices: ${res.message}")
                return@supervisorScope
            }
        }

        val futuresPrices = when (val res = futuresPricesDeferred.await()) {
            is Resource.Success -> res.data!!.associateBy { it.uid }
            is Resource.Error -> {
                logger.info("Unable to load futures prices: ${res.message}")
                return@supervisorScope
            }
        }

        usersWithFollowedShares.forEach { user ->
            handleUserFutures(
                user = user,
                sharesToFutures = sharesToFutures,
                sharesPrices = sharesPrices,
                futuresPrices = futuresPrices
            )
        }

        val cache = constructIndicatorCache(shares)
        usersWithFollowedShares.forEach {
            handleIndicatorsForUser(user = it, cache = cache)
        }
    }

    private suspend fun handleUserFutures(
        user: UserWithFollowedShares,
        sharesToFutures: Map<String, List<TinkoffFuture>>,
        sharesPrices: Map<String, TinkoffPrice>,
        futuresPrices: Map<String, TinkoffPrice>
    ) {
        val handled = mutableListOf<UserShare>()
        logger.info("User id: ${user.id}")
        user.shares.forEach { share ->
            val sharePrice = sharesPrices[share.uid] ?: return@forEach
            val futures = sharesToFutures[share.ticker] ?: return@forEach
            if (futures.isEmpty()) return@forEach

            val futuresToNotify = mutableListOf<NotifyFuture>()
            futures.forEach { future ->
                val futurePrice = futuresPrices.getOrElse(future.uid) { TinkoffPrice() }
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
            if (share.futuresNotified == shouldNotify) return@forEach
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

            if (user.agentNotifications && user.agentChatId != null) {
                val agentUpdate = if (shouldNotify)
                    AgentShareUpdate(chatId = user.agentChatId, share = notifyShare)
                else
                    AgentSharePriceInsufficientUpdate(chatId = user.agentChatId, share = notifyShare)
                _agentUpdates.emit(agentUpdate)
            }
        }
        logger.info("Handled ${handled.size} shares for user ${user.id}")
        database.updateUserShares(handled)
    }

    private suspend fun constructCache(usersWithFollowedShares: List<UserWithFollowedShares>): Cache? {
        if (usersWithFollowedShares.isEmpty()) return null

        val shares = extractUniqueShares(usersWithFollowedShares)
        val sharesPricesCache = HashMap<String, TinkoffCandle>(shares.size)
        val hourlyRsiCache = HashMap<String, Double>(shares.size)
        val dailyRsiCache = HashMap<String, Double>(shares.size)

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
            val dailyCandlesResource = tinkoff.getDailyCandles(share.uid)
            if (dailyCandlesResource.data.isNullOrEmpty()) {
                logger.info("Unable to get daily candles for ${share.ticker} because of ${dailyCandlesResource.message}")
                continue
            }
            val dailyCandles = dailyCandlesResource.data
            val dailyPrices = extractPrices(dailyCandles)

            val hourlyCandlesResource = tinkoff.getHourlyCandles(share.uid)
            if (hourlyCandlesResource.data.isNullOrEmpty()) {
                logger.info("Unable to get hourly candles for ${share.ticker} because of ${hourlyCandlesResource.message}")
                continue
            }
            val hourlyCandles = hourlyCandlesResource.data
            val hourlyPrices = extractPrices(hourlyCandles)

            dailyRsiCache[share.ticker] = MathUtil.calculateRsi(dailyPrices)
            hourlyRsiCache[share.ticker] = MathUtil.calculateRsi(hourlyPrices)
            sharesPricesCache[share.ticker] = hourlyCandles.last()

            delay(10)
        }


        val futuresPrice = mutableMapOf<String, TinkoffCandle>()
        for (future in futuresList) {
            val hourlyCandlesResource = tinkoff.getHourlyCandles(future.uid)
            if (hourlyCandlesResource.data.isNullOrEmpty()) {
                logger.info("Unable to get hourly candles for future ${future.ticker} because of ${hourlyCandlesResource.message}")
                continue
            }
            futuresPrice[future.ticker] = hourlyCandlesResource.data.last()
            delay(10)
        }

        return Cache(
            shares = sharesPricesCache,
            sharesToFutures = sharesToFutures,
            futures = futuresPrice,
            hourlyRsiCache = hourlyRsiCache,
            dailyRsiCache = dailyRsiCache
        )
    }


    private suspend fun handleIndicatorsForUser(user: UserWithFollowedShares, cache: IndicatorCache) {
        val handled = mutableListOf<UserShare>()

        for (share in user.shares) {
            val updateData = mutableListOf<IndicatorUpdateData>()
            val dailyRsi = cache.dailyRsiCache[share.ticker] ?: continue
            val hourlyRsi = cache.hourlyRsiCache[share.ticker] ?: continue
            val price = cache.prices[share.ticker] ?: continue

            if (dailyRsi > MathUtil.RSI_HIGH && hourlyRsi > MathUtil.RSI_HIGH) {
                updateData.add(
                    IndicatorUpdateData.RsiHighData(
                        hourlyRsi = hourlyRsi,
                        dailyRsi = dailyRsi
                    )
                )
            } else if (dailyRsi < MathUtil.RSI_LOW && hourlyRsi < MathUtil.RSI_LOW) {
                updateData.add(
                    IndicatorUpdateData.RsiLowData(
                        hourlyRsi = hourlyRsi,
                        dailyRsi = dailyRsi
                    )
                )
            }

            val shouldNotify = updateData.isNotEmpty()
            //TODO: Make more flexible system
            if (shouldNotify == share.indicatorsNotified) continue
            val handledShare = share.copy(indicatorsNotified = shouldNotify)
            handled.add(handledShare)

            if (shouldNotify) {
                val update = TelegramIndicatorUpdate(
                    userId = user.id,
                    ticker = share.ticker,
                    price = price,
                    data = updateData
                )
                _updates.emit(update)

                if (user.agentNotifications && user.agentChatId != null) {
                    val agentUpdate = AgentIndicatorUpdate(
                        chatId = user.agentChatId,
                        ticker = share.ticker,
                        price = price,
                        data = updateData
                    )
                    _agentUpdates.emit(agentUpdate)
                }
            }
        }
        database.updateUserShares(handled)
    }

    private suspend fun getSharesPrices(sharesTickers: Iterable<String>): Resource<List<TinkoffPrice>> {
        val shares = sharesTickers.mapNotNull {
            tinkoff.getSecurity(it).data
        }

        return try {
            val res = tinkoff.getSharesPrice(shares)
            res
        } catch (e: Exception) {
            logger.info(e.message)
            Resource.Error(e.message)
        }
    }

    private suspend fun getFuturesPrices(futuresList: List<TinkoffFuture>): Resource<List<TinkoffPrice>> {
        return try {
            tinkoff.getFuturesPrices(futuresList)
        } catch (e: Exception) {
            logger.info(e.message)
            Resource.Error(e.message)
        }
    }

    private fun extractPrices(candles: List<TinkoffCandle>): DoubleArray {
        val res = DoubleArray(candles.size)
        candles.forEachIndexed { index, candle ->
            res[index] = candle.close
        }
        return res
    }

    companion object {
        private const val MILLIS_MINUTE = 60000L
    }
}