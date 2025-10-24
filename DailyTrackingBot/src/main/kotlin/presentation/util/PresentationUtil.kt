package presentation.util

import domain.util.MathUtil

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
}