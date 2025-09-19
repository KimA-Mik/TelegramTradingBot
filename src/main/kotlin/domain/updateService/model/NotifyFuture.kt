package domain.updateService.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant


data class NotifyFuture @OptIn(ExperimentalTime::class) constructor(
    val ticker: String,
    val name: String,
    val price: Double,
    val actualDifference: Double,
    val annualPercent: Double,
    val annualAfterTaxes: Double,
    val expirationDate: Instant
)