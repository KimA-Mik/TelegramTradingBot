package domain.tinkoff.model

import kotlinx.datetime.Instant

data class TinkoffCandle(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val time: Instant,
    val isComplete: Boolean
)
