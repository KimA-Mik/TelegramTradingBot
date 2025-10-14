package domain.updateservice

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
            if (!security.isActive) continue
            var currentSecurity = security
            val lastPrice = lastPrices[security.uid]?.price ?: continue

            val currentDeviation = MathUtil.absolutePercentageDifference(lastPrice, currentSecurity.targetPrice)
            val shouldNotify = currentDeviation < security.targetDeviation
            val indicators = indicatorsCache[security.uid]
            if (shouldNotify && currentSecurity.shouldNotify) {
                _updates.emit(
                    TelegramUpdate.PriceAlert(
                        user = user.user,
                        security = security,
                        currentPrice = lastPrice,
                        currentDeviation = currentDeviation,
                        indicators = indicators
                    )
                )

                currentSecurity = security.copy(shouldNotify = false)
            } else if (!shouldNotify && !currentSecurity.shouldNotify) {
                currentSecurity = security.copy(shouldNotify = true)
            }

            if (indicators == null) continue
            val rsi = indicators.min15Rsi
            val shouldNotifyRsi = rsi <= MathUtil.RSI_LOW || rsi >= MathUtil.RSI_HIGH
            if (shouldNotifyRsi && currentSecurity.shouldNotifyRsi) {
                _updates.emit(
                    TelegramUpdate.RsiAlert(
                        user = user.user,
                        security = security,
                        currentPrice = lastPrice,
                        currentRsi = rsi,
                        indicators = indicators
                    )
                )

                currentSecurity = currentSecurity.copy(shouldNotifyRsi = false)
            } else if (!shouldNotifyRsi && !currentSecurity.shouldNotifyRsi) {
                currentSecurity = currentSecurity.copy(shouldNotifyRsi = true)
            }

            if (currentSecurity != security) {
                outTrackingSecurities.add(currentSecurity)
            }
        }
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