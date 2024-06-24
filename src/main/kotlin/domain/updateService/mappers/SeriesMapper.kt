package domain.updateService.mappers

import domain.tinkoff.model.TinkoffCandle
import domain.utils.DateUtil
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.num.DoubleNum


fun List<TinkoffCandle>.toSeries(seriesName: String = String()): BarSeries {
    val series = BaseBarSeriesBuilder().withName(seriesName).withNumTypeOf(DoubleNum::class.java).build()

    this.forEach { candle ->
        series.addBar(
            candle.time.toJavaInstant().atZone(DateUtil.timezoneMoscow.toJavaZoneId()),
            candle.open,
            candle.high,
            candle.low,
            candle.close
        )
    }

    return series
}