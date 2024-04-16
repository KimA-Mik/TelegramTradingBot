package domain.tinkoff.model

import kotlinx.datetime.Instant

data class TinkoffFuture(
    val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0,
    val expirationDate: Instant
)
