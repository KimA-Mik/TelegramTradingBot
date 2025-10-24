package domain.util

import kotlin.math.abs

object MathUtil {
    const val RSI_LOW = 22.0
    const val RSI_HIGH = 78.0
    const val BOLLINGER_BARS_COUNT = 20
    const val BB_CRITICAL_HIGH = 0.95
    const val BB_CRITICAL_LOW = 0.05
    fun absolutePercentageDifference(oldValue: Double, newValue: Double): Double {
        if (oldValue + newValue == 0.0) return 0.0
        //https://www.calculatorsoup.com/calculators/algebra/percent-difference-calculator.php
        return abs(newValue - oldValue) * 2 / (newValue + oldValue) * 100
    }

    fun anotherAbsolutePercentageDifference(oldValue: Double, newValue: Double): Double {
        if (oldValue == 0.0) {
            return 0.0
        }
        return abs((newValue - oldValue) / oldValue) * 100
    }
}