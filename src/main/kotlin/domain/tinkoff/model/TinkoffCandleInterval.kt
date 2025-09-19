package domain.tinkoff.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

enum class TinkoffCandleInterval(val duration: Duration) {
    CANDLE_INTERVAL_UNSPECIFIED(Duration.ZERO),
    CANDLE_INTERVAL_1_MIN(1.minutes),
    CANDLE_INTERVAL_2_MIN(2.minutes),
    CANDLE_INTERVAL_3_MIN(3.minutes),
    CANDLE_INTERVAL_5_MIN(5.minutes),
    CANDLE_INTERVAL_10_MIN(10.minutes),
    CANDLE_INTERVAL_15_MIN(15.minutes),
    CANDLE_INTERVAL_30_MIN(30.minutes),
    CANDLE_INTERVAL_HOUR(1.hours),
    CANDLE_INTERVAL_2_HOUR(2.hours),
    CANDLE_INTERVAL_4_HOUR(4.hours),
    CANDLE_INTERVAL_DAY(1.days),
    CANDLE_INTERVAL_WEEK(7.days),
    CANDLE_INTERVAL_MONTH(30.days);
}