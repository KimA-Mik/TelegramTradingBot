package domain.analysis.mappers

import domain.tinkoff.model.TinkoffCandle
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.bars.TimeBarBuilder
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
fun List<TinkoffCandle>.toSeries(seriesName: String = String()): BarSeries {
    val series = BaseBarSeriesBuilder().withName(seriesName).build()

    this.forEach { candle ->
        val bar = TimeBarBuilder()
            //.duration
            .endTime(candle.time.toJavaInstant())
            .openPrice(candle.open)
            .highPrice(candle.high)
            .lowPrice(candle.low)
            .closePrice(candle.close)
            //.volume()
            .build()

        series.addBar(bar)
    }

    return series
}