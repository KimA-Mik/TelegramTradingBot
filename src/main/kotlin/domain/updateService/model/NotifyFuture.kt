package domain.updateService.model

data class NotifyFuture(
    val ticker: String,
    val name: String,
    val price: Double,
    val actualDifference: Double
)