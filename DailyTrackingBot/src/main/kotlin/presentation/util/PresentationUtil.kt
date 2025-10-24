package presentation.util

import domain.util.MathUtil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object PresentationUtil {
    const val GREEN = "🟢"
    const val RED = "🔴"
    const val YELLOW = "🟡"

    fun rsiColor(value: Double): String {
        return when {
            value >= MathUtil.RSI_HIGH -> RED
            value <= MathUtil.RSI_LOW -> GREEN
            else -> YELLOW
        }
    }

    fun markupBbColor(
        value: Double,
        low: Double,
        high: Double,
        lowPercent: Double = 0.4,
        highPercent: Double = 0.6
    ): String {
        if (high == low) return YELLOW // avoid division by zero
        val percent = (value - low) / (high - low)
        return when {
            percent < lowPercent -> GREEN
            percent > highPercent -> RED
            else -> YELLOW
        }
    }

    const val T_INVEST_TITLE = "T-Инвестиции"

    @OptIn(ExperimentalTime::class)
    fun renderLongTimestamp(timestamp: Long) = Instant
        .fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(TelegramUtil.localDateTimeFormat)
}