package domain.updateService.model

import kotlinx.datetime.Instant

data class NotifyFuture(
    val ticker: String,
    val name: String,
    val price: Double,
    val actualDifference: Double,
    val annualPercent: Double,
    val expirationDate: Instant
)