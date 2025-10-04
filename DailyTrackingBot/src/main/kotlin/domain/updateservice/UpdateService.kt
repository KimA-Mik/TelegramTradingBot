package domain.updateservice

import domain.updateservice.indicators.IndicatorsCache
import domain.user.model.User
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
        val users = repository.getAllUsers()
        val securities = getSecurities(users)
        handleUsers(users, securities)
    }

    private suspend fun getSecurities(users: List<User>): Map<String, Security> = coroutineScope {
        val sharesDeferred = async { cacheServerApi.tradableShares(InstrumentsRequest.default) }
        val futuresDeferred = async { cacheServerApi.tradableFutures(InstrumentsRequest.default) }
        val tickers = users.map { it.ticker }.toSet()

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
    private suspend fun handleUsers(users: List<User>, securities: Map<String, Security>) {
        val indicatorsCache = IndicatorsCache(cacheServerApi)
        val uids = securities.values.map { it.uid }
        val lastPrices = cacheServerApi.lastPrices(GetLastPricesRequest.default(uids))
            .getOrElse { return }.associateBy { it.uid }
        for (user in users) {
            if (!user.isActive) continue
            if (user.ticker == null) continue
            if (user.targetPrice == null) continue
            if (user.targetDeviation == null) continue
            val security = securities[user.ticker] ?: continue
            val lastPrice = lastPrices[security.uid]?.price ?: continue

            val currentDeviation = MathUtil.absolutePercentageDifference(lastPrice, user.targetPrice)
            val shouldNotify = currentDeviation < user.targetDeviation
            val indicators = indicatorsCache[security.uid]
            if (shouldNotify && user.shouldNotify) {
                _updates.emit(
                    TelegramUpdate.PriceAlert(
                        user = user,
                        security = security,
                        currentPrice = lastPrice,
                        currentDeviation = currentDeviation,
                        indicators = indicators
                    )
                )

                repository.updateUser(user.copy(shouldNotify = false))
            } else if (!shouldNotify && !user.shouldNotify) {
                repository.updateUser(user.copy(shouldNotify = true))
            }

            if (indicators == null) continue
            val rsi = indicators.min15Rsi
            val shouldNotifyRsi = rsi < MathUtil.RSI_LOW || rsi > MathUtil.RSI_HIGH
            if (shouldNotifyRsi && user.shouldNotifyRsi) {
                _updates.emit(
                    TelegramUpdate.RsiAlert(
                        user = user,
                        security = security,
                        currentPrice = lastPrice,
                        currentRsi = rsi,
                        indicators = indicators
                    )
                )

                repository.updateUser(user.copy(shouldNotifyRsi = false))
            } else if (!shouldNotifyRsi && !user.shouldNotifyRsi) {
                repository.updateUser(user.copy(shouldNotifyRsi = true))
            }
        }
    }

    fun User.skip(): Boolean {
        if (!isActive) return true
        if (ticker == null) return true
        if (targetPrice == null) return true
        if (targetDeviation == null) return true
        return false

//        val now = Clock.System.now().toLocalDateTime(DateUtil.timezoneMoscow)
//        if (now.dayOfWeek in weekends) {
//            return true
//        }
//        if (now.hour < 10 || (now.hour == 10 && now.minute < 0)) {
//            return true
//        }
//        if (now.hour > 19 || (now.hour == 19 && now.minute > 0)) {
//            return true
//        }
//        return false
    }

    @OptIn(ExperimentalTime::class)
    suspend fun resetUsers() {
        val currentTime = Clock.System.now().toLocalDateTime(DateUtil.timezoneMoscow).time
        if (currentTime < LocalTime(23, 50)) return

        val users = repository.getAllUsers()
        for (user in users) {
            if (!user.isActive) continue
            if (user.ticker == null) continue
            if (user.targetPrice == null) continue
            if (user.targetDeviation == null) continue

            if (!user.remainActive) {
                repository.updateUser(user.copy(isActive = false, shouldNotify = true))
            }
        }
    }
}