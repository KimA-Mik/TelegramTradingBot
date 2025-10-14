package presentation.util

import domain.util.MathUtil

object PresentationUtil {
    const val GREEN = "🟢"
    const val RED = "🔴"
    const val YELLOW = "🟡"

    fun rsiColor(value: Double): String {
        return when {
            value >= MathUtil.RSI_HIGH -> GREEN
            value <= MathUtil.RSI_LOW -> RED
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
            percent < lowPercent -> RED
            percent > highPercent -> GREEN
            else -> YELLOW
        }
    }
}