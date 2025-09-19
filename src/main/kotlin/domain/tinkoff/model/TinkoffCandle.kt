package domain.tinkoff.model

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class TinkoffCandle @OptIn(ExperimentalTime::class) constructor(
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0,
    val volume: Long = 0,
    val time: Instant = Instant.fromEpochMilliseconds(0),
    val isComplete: Boolean = false,
    val duration: Duration = Duration.ZERO
)
