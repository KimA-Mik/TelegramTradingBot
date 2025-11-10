package ru.kima.telegrambot.common.techanalysis.mappers

import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.bars.TimeBarBuilder
import org.ta4j.core.num.DoubleNumFactory
import ru.kima.cacheserver.api.schema.model.HistoricCandle
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
fun List<HistoricCandle>.toSeries(
    duration: Duration,
    seriesName: String = ""
): BarSeries {
    val series = BaseBarSeriesBuilder()
        .withNumFactory(DoubleNumFactory.getInstance())
        .withName(seriesName).build()
    this.forEach { candle ->
        val bar = TimeBarBuilder()
            .timePeriod(duration.toJavaDuration())
            .endTime(candle.time.toJavaInstant())
            .openPrice(candle.open)
            .highPrice(candle.high)
            .lowPrice(candle.low)
            .closePrice(candle.close)
            .volume(candle.volume)
            .build()

        series.addBar(bar)
    }

    return series
}