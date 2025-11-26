package presentation.util

import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
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

    fun rsiColor(
        value: Double,
        low: Double = MathUtil.RSI_LOW,
        high: Double = MathUtil.RSI_HIGH
    ): String {
        return when {
            value >= high -> RED
            value <= low -> GREEN
            else -> YELLOW
        }
    }

    fun markupBbColor(
        value: Double,
        low: Double,
        high: Double,
        lowPercent: Double = MathUtil.BB_CRITICAL_LOW,
        highPercent: Double = MathUtil.BB_CRITICAL_HIGH
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

    fun formatTargetPrice(price: Double?): String {
        return when {
            price == null -> "не установлена"
            else -> price.formatToRu() + ROUBLE_SIGN
        }
    }
}