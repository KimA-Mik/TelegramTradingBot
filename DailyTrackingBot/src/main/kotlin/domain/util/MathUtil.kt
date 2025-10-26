package domain.util

import domain.techanalysis.BollingerBands
import kotlin.math.abs

object MathUtil {
    const val RSI_LOW = 22.0
    const val RSI_HIGH = 78.0
    fun isRsiCritical(rsi: Double) =
        rsi <= RSI_LOW || rsi >= RSI_HIGH

    const val BOLLINGER_BARS_COUNT = 20
    const val BB_CRITICAL_HIGH = 0.95
    const val BB_CRITICAL_LOW = 0.05
    fun isBbCritical(
        value: Double,
        bb: BollingerBands.BollingerBandsData,
        lowPercent: Double = BB_CRITICAL_LOW,
        highPercent: Double = BB_CRITICAL_HIGH
    ): Boolean {
        if (bb.upper == bb.lower) return false // avoid division by zero
        val percent = (value - bb.lower) / (bb.upper - bb.lower)
        return percent <= lowPercent || percent >= highPercent
    }

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