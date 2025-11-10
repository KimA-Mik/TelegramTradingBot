package ru.kima.telegrambot.common.techanalysis

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import ru.kima.telegrambot.common.util.MathUtil

//TODO: Factor out to common module
object BollingerBands {
    fun calculate(series: BarSeries, barsCount: Int = MathUtil.BOLLINGER_BARS_COUNT): BollingerBandsData {
        val closes = ClosePriceIndicator(series)
        val middle = BollingerBandsMiddleIndicator(SMAIndicator(closes, barsCount))

        val deviation = StandardDeviationIndicator(closes, barsCount)
        val lower = BollingerBandsLowerIndicator(middle, deviation)
        val upper = BollingerBandsUpperIndicator(middle, deviation)

        return BollingerBandsData(
            lower = lower,
            middle = middle,
            upper = upper
        )
    }

    data class BollingerBandsData(
        val lower: BollingerBandsLowerIndicator,
        val middle: BollingerBandsMiddleIndicator,
        val upper: BollingerBandsUpperIndicator
    )
}