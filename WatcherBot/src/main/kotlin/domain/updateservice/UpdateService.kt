package domain.updateservice

import domain.user.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.telegrambot.common.util.DateUtil
import ru.kima.telegrambot.common.util.TimeUtil
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
            delayNonWorkingHours(8, 45, 23, 59)
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
    }
}
