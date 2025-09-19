package domain.tinkoff.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant


data class TinkoffOrderBook @OptIn(ExperimentalTime::class) constructor(
    val uid: String,
    val depth: Int,
    val bids: List<TinkoffOrder>,
    val asks: List<TinkoffOrder>,
    val lastPrice: Double,
    val closePrice: Double,
    val lastPriceTs: Instant,
    val closePriceTs: Instant,
    val orderBookTs: Instant,
)