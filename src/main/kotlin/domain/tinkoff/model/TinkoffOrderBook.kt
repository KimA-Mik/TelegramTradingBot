package domain.tinkoff.model

import kotlinx.datetime.Instant

data class TinkoffOrderBook(
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