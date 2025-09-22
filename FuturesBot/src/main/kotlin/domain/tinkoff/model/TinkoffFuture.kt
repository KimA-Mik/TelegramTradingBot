package domain.tinkoff.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class TinkoffFuture @OptIn(ExperimentalTime::class) constructor(
    val uid: String = String(),
    val ticker: String = String(),
    val name: String = String(),
    val lot: Int = 0,
    val expirationDate: Instant
)
