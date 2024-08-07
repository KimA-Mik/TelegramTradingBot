package domain.analysis.util

import org.ta4j.core.indicators.CachedIndicator
import org.ta4j.core.num.Num

fun CachedIndicator<Num>.getLast(): Double {
    return this.getValue(barSeries.endIndex).doubleValue()
}