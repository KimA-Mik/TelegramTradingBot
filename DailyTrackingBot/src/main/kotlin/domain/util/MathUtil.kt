package domain.util

import domain.techanalysis.BollingerBands
import kotlin.math.abs

object MathUtil {
    const val RSI_LOW = 22.0
    const val RSI_HIGH = 78.0
    fun isRsiCritical(rsi: Double, low: Double = RSI_LOW, high: Double = RSI_HIGH) =
        rsi <= low || rsi >= high

    const val BB_FOR_RSI_HIGH = 80.0
    const val BB_FOR_RSI_LOW = 20.0
    fun shouldNotifyRsi(
        rsi: Double,
        price: Double,
        bb: BollingerBands.BollingerBandsData,
        bbLowPercent: Double = BB_FOR_RSI_LOW,
        bbHighPercent: Double = BB_FOR_RSI_HIGH
    ) = isRsiCritical(rsi) && isBbCritical(price, bb, bbLowPercent, bbHighPercent)

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

    const val RSI_FOR_BB_HIGH = 65.0
    const val RSI_FOR_BB_LOW = 35.0
    fun shouldNotifyBb(
        rsi: Double,
        price: Double,
        bb: BollingerBands.BollingerBandsData,
        lowRsi: Double = RSI_FOR_BB_LOW,
        highRsi: Double = RSI_FOR_BB_HIGH,
    ) = isBbCritical(price, bb) && isRsiCritical(rsi, lowRsi, highRsi)

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