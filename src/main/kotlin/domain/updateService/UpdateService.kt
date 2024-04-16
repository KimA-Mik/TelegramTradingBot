package domain.updateService

import Resource
import domain.common.getFutureSharePrice
import domain.common.percentBetweenDoubles
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import domain.tinkoff.util.TinkoffFutureComparator
import domain.updateService.model.NotifyFuture
import domain.updateService.model.NotifyShare
import domain.updateService.model.UserWithFollowedShares
import domain.updateService.updates.SharePriceInsufficientUpdate
import domain.updateService.updates.ShareUpdate
import domain.updateService.updates.Update
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
    private val _updates = MutableSharedFlow<Update>()
    val updates = _updates.asSharedFlow()

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

        val sharesTickers = mutableSetOf<String>()
        usersWithFollowedShares.forEach { userWithFollowedShares ->
            userWithFollowedShares.shares.forEach { share ->
                sharesTickers.add(share.ticker)
            }
        }

        val sharesPricesDeferred = async {
            getSharesPrices(sharesTickers)
        }

        val sharesToFutures = sharesTickers.associateBy({ it }, {
            val temp = TinkoffShare(ticker = it)
            val resource = tinkoff.getSecurityFutures(temp)
            resource
                .data
                ?.sortedWith(TinkoffFutureComparator)
                ?.take(4)
                ?: emptyList()
        })

        val futuresList = sharesToFutures.flatMap { it.value }
        val futuresPricesDeferred = async {
            getFuturesPrices(futuresList)
        }

        val sharesPrices = when (val res = sharesPricesDeferred.await()) {
            is Resource.Success -> res.data!!.associateBy { it.uid }
            is Resource.Error -> {
                logger.info("Unable to load futures prices: ${res.message}")
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
            handleUser(
                user = user,
                sharesToFutures = sharesToFutures,
                sharesPrices = sharesPrices,
                futuresPrices = futuresPrices
            )
        }
    }

    private suspend fun handleUser(
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
                val percent = percentBetweenDoubles(sharePrice.price, futureSlotPrice)
                val annualPercent = FuturesUtil.getFutureAnnualPercent(percent, future.expirationDate)
                if (abs(annualPercent) >= share.percent) {
                    futuresToNotify.add(
                        NotifyFuture(
                            ticker = future.ticker,
                            name = future.name,
                            price = futurePrice.price,
                            actualDifference = percent,
                            annualPercent = annualPercent
                        )
                    )
                }
            }

            val shouldNotify = futuresToNotify.isNotEmpty()
            if (share.notified == shouldNotify) return@forEach
            handled.add(share.copy(notified = shouldNotify))
            val notifyShare = NotifyShare(
                shareTicker = share.ticker,
                sharePrice = sharePrice.price,
                minimalDifference = share.percent,
                futures = futuresToNotify
            )
            val update = if (shouldNotify)
                ShareUpdate(userId = user.id, share = notifyShare)
            else
                SharePriceInsufficientUpdate(userId = user.id, share = notifyShare)
            _updates.emit(update)
        }
        logger.info("Handled ${handled.size} shares for user ${user.id}")
        database.updateUserSharesNotified(handled)
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

    companion object {
        private const val MILLIS_MINUTE = 60000L
    }
}