package domain.tinkoff.model

import java.time.LocalDateTime

data class TinkoffPrice(
    val ticker: String = String(),
    val uid: String = String(),
    val price: Double = 0.0,
    val dateTime: LocalDateTime = LocalDateTime.now()
)
