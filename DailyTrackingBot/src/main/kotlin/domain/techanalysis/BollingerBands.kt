package domain.techanalysis

import domain.util.MathUtil
import domain.util.lastDouble
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator

//TODO: Factor out to common module
object BollingerBands {
    fun calculate(series: BarSeries, barsCount: Int = MathUtil.BOLLINGER_BARS_COUNT): BollingerBandsData {
        val closes = ClosePriceIndicator(series)
        val middle = BollingerBandsMiddleIndicator(SMAIndicator(closes, barsCount))

        val deviation = StandardDeviationIndicator(closes, barsCount)
        val lower = BollingerBandsLowerIndicator(middle, deviation)
        val upper = BollingerBandsUpperIndicator(middle, deviation)

        return BollingerBandsData(
            lower = lower.lastDouble(),
            middle = middle.lastDouble(),
            upper = upper.lastDouble()
        )
    }

    data class BollingerBandsData(
        val lower: Double,
        val middle: Double,
        val upper: Double
    )
}