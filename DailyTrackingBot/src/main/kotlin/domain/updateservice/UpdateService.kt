package domain.updateservice

import domain.updateservice.indicators.CacheEntry
import domain.updateservice.indicators.IndicatorsCache
import domain.user.model.FullUser
import domain.user.model.TrackingSecurity
import domain.user.repository.UserRepository
import domain.util.DateUtil
import domain.util.MathUtil
import domain.util.TimeUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.LastPrice
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.requests.GetLastPricesRequest
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UpdateService(
    private val repository: UserRepository,
    private val cacheServerApi: CacheServerApi,
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
            val delayTime = Random.nextFloat() * 30f * TimeUtil.SECOND_MILLIS
            delay(30 * TimeUtil.SECOND_MILLIS + delayTime.toLong())
            checkForUpdates()
            resetUsers()
            delayNonWorkingHours(9, 50, 23, 59)
        }
    }

    private val weekends = setOf<DayOfWeek>(/*DayOfWeek.SATURDAY, DayOfWeek.SUNDAY*/)

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

    suspend fun checkForUpdates() {
        val users = repository.getFullUsers()
        val securities = getSecurities(users)
        handleUsers(users, securities)
    }

    private suspend fun getSecurities(users: List<FullUser>): Map<String, Security> = coroutineScope {
        val sharesDeferred = async { cacheServerApi.tradableShares(InstrumentsRequest.default) }
        val futuresDeferred = async { cacheServerApi.tradableFutures(InstrumentsRequest.default) }
        val tickers = users.flatMap { it.securities }.map { it.ticker }.toSet()

        val shares = sharesDeferred.await().getOrElse {
            logger.error(it.message)
            emptyList()
        }

        val futures = futuresDeferred.await().getOrElse {
            logger.error(it.message)
            emptyList()
        }

        val securities = (shares + futures).filter { it.ticker in tickers }.associateBy { it.ticker }
        return@coroutineScope securities
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleUsers(users: List<FullUser>, securities: Map<String, Security>) {
        val indicatorsCache = IndicatorsCache(cacheServerApi)
        val uids = securities.values.map { it.uid }
        val lastPrices = cacheServerApi.lastPrices(GetLastPricesRequest.default(uids))
            .getOrElse { return }.associateBy { it.uid }
        val toUpdate = mutableListOf<TrackingSecurity>()
        for (user in users) {
            handleUser(user, lastPrices, indicatorsCache, toUpdate)
        }

        if (toUpdate.isNotEmpty()) {
            repository.updateTrackingSecurities(toUpdate)
        }
    }

    private suspend fun handleUser(
        user: FullUser,
        lastPrices: Map<String, LastPrice>,
        indicatorsCache: IndicatorsCache,
        outTrackingSecurities: MutableList<TrackingSecurity>
    ) {
        for (security in user.securities) {
            val lastPrice = lastPrices[security.uid]?.price ?: continue
            val indicators = indicatorsCache[security.uid]

            var currentSecurity = handlePrice(user, security, indicators, lastPrice)
            currentSecurity = handleRsi(user, currentSecurity, indicators, lastPrice)
            currentSecurity = handleBB(user, currentSecurity, indicators, lastPrice)

            if (currentSecurity != security) {
                outTrackingSecurities.add(currentSecurity)
            }
        }
    }

    private suspend fun handlePrice(
        user: FullUser,
        security: TrackingSecurity,
        indicators: CacheEntry?,
        lastPrice: Double
    ): TrackingSecurity {
        if (!security.isActive) return security
        val currentDeviation = MathUtil.absolutePercentageDifference(lastPrice, security.targetPrice)
        val currentLowDeviation = MathUtil.absolutePercentageDifference(lastPrice, security.lowTargetPrice)
        val shouldNotify = currentDeviation < security.targetDeviation
        val shouldNotifyLow = currentLowDeviation < security.targetDeviation
        if ((shouldNotifyLow || shouldNotify) && security.shouldNotify) {
            _updates.emit(
                TelegramUpdate.PriceAlert(
                    user = user.user,
                    security = security,
                    currentPrice = lastPrice,
                    indicators = indicators,
                    type = when {
                        shouldNotify && shouldNotifyLow -> TelegramUpdate.PriceAlert.PriceType.All(
                            deviation = currentDeviation,
                            lowDeviation = currentLowDeviation
                        )

                        shouldNotify -> TelegramUpdate.PriceAlert.PriceType.Target(currentDeviation)
                        else -> TelegramUpdate.PriceAlert.PriceType.LowTarget(currentLowDeviation)
                    }
                )
            )

            return security.copy(shouldNotify = false)
        } else if (!shouldNotify && !shouldNotifyLow && !security.shouldNotify) {
            return security.copy(shouldNotify = true)
        }

        return security
    }


    private suspend fun handleRsi(
        user: FullUser,
        security: TrackingSecurity,
        indicators: CacheEntry?,
        lastPrice: Double,
    ): TrackingSecurity {
        if (indicators == null) return security
        val intervals = mutableListOf<TelegramUpdate.RsiAlert.RsiInterval>()
        if (MathUtil.isRsiCritical(indicators.min15Rsi)) intervals.add(TelegramUpdate.RsiAlert.RsiInterval.MIN15)
        if (MathUtil.isRsiCritical(indicators.hour4Rsi)) intervals.add(TelegramUpdate.RsiAlert.RsiInterval.HOUR4)

        val shouldNotifyRsi = intervals.isNotEmpty()
        if (shouldNotifyRsi && security.shouldNotifyRsi) {
            _updates.emit(
                TelegramUpdate.RsiAlert(
                    user = user.user,
                    security = security,
                    currentPrice = lastPrice,
                    intervals = intervals,
                    indicators = indicators
                )
            )

            return security.copy(shouldNotifyRsi = false)
        } else if (!shouldNotifyRsi && !security.shouldNotifyRsi) {
            return security.copy(shouldNotifyRsi = true)
        }

        return security
    }

    private suspend fun handleBB(
        user: FullUser,
        security: TrackingSecurity,
        indicators: CacheEntry?,
        lastPrice: Double,
    ): TrackingSecurity {
        if (indicators == null) return security
        val intervals = mutableListOf<TelegramUpdate.BbAlert.BbInterval>()
        if (MathUtil.isBbCritical(lastPrice, indicators.min15bb)) intervals.add(TelegramUpdate.BbAlert.BbInterval.MIN15)
        if (MathUtil.isBbCritical(lastPrice, indicators.hour4Bb)) intervals.add(TelegramUpdate.BbAlert.BbInterval.HOUR4)

        val shouldNotifyBb = intervals.isNotEmpty()
        if (shouldNotifyBb && security.shouldNotifyBb) {
            _updates.emit(
                TelegramUpdate.BbAlert(
                    user = user.user,
                    security = security,
                    currentPrice = lastPrice,
                    intervals = intervals,
                    indicators = indicators
                )
            )

            return security.copy(shouldNotifyBb = false)
        } else if (!shouldNotifyBb && !security.shouldNotifyBb) {
            return security.copy(shouldNotifyBb = true)
        }

        return security
    }

    @OptIn(ExperimentalTime::class)
    suspend fun resetUsers() {
        val currentTime = Clock.System.now().toLocalDateTime(DateUtil.timezoneMoscow).time
        if (currentTime < LocalTime(23, 50)) return

        val users = repository.getFullUsers()
        val toUpdate = mutableListOf<TrackingSecurity>()
        for (user in users) {
            for (security in user.securities) {
                if (!security.isActive) continue
                if (!security.remainActive) {
                    toUpdate.add(
                        security.copy(isActive = false, shouldNotify = true, shouldNotifyRsi = true)
                    )
                }
            }
        }
        repository.updateTrackingSecurities(toUpdate)
    }
}