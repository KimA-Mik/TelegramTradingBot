package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffCandleInterval
import domain.tinkoff.model.TinkoffCandleInterval.*
import ru.tinkoff.piapi.contract.v1.CandleInterval

fun TinkoffCandleInterval.toCandleInterval(): CandleInterval {
    return when (this) {
        CANDLE_INTERVAL_UNSPECIFIED -> CandleInterval.CANDLE_INTERVAL_UNSPECIFIED
        CANDLE_INTERVAL_1_MIN -> CandleInterval.CANDLE_INTERVAL_1_MIN
        CANDLE_INTERVAL_5_MIN -> CandleInterval.CANDLE_INTERVAL_5_MIN
        CANDLE_INTERVAL_15_MIN -> CandleInterval.CANDLE_INTERVAL_15_MIN
        CANDLE_INTERVAL_HOUR -> CandleInterval.CANDLE_INTERVAL_HOUR
        CANDLE_INTERVAL_DAY -> CandleInterval.CANDLE_INTERVAL_DAY
        CANDLE_INTERVAL_2_MIN -> CandleInterval.CANDLE_INTERVAL_2_MIN
        CANDLE_INTERVAL_3_MIN -> CandleInterval.CANDLE_INTERVAL_3_MIN
        CANDLE_INTERVAL_10_MIN -> CandleInterval.CANDLE_INTERVAL_10_MIN
        CANDLE_INTERVAL_30_MIN -> CandleInterval.CANDLE_INTERVAL_30_MIN
        CANDLE_INTERVAL_2_HOUR -> CandleInterval.CANDLE_INTERVAL_2_HOUR
        CANDLE_INTERVAL_4_HOUR -> CandleInterval.CANDLE_INTERVAL_4_HOUR
        CANDLE_INTERVAL_WEEK -> CandleInterval.CANDLE_INTERVAL_WEEK
        CANDLE_INTERVAL_MONTH -> CandleInterval.CANDLE_INTERVAL_MONTH
    }
}