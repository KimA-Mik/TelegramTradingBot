package domain.analysis.transformations

import domain.analysis.model.BollingerBandsData
import domain.analysis.util.getLast
import domain.math.MathUtil
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator

object BollingerBands {
    fun calculate(series: BarSeries, barsCount: Int = MathUtil.BOLLINGER_BARS_COUNT): BollingerBandsData {
        val closes = ClosePriceIndicator(series)
        val middle = BollingerBandsMiddleIndicator(SMAIndicator(closes, barsCount))

        val deviation = StandardDeviationIndicator(closes, barsCount)
        val lower = BollingerBandsLowerIndicator(middle, deviation)
        val upper = BollingerBandsUpperIndicator(middle, deviation)

        return BollingerBandsData(
            lower = lower.getLast(),
            middle = middle.getLast(),
            upper = upper.getLast()
        )
    }
}