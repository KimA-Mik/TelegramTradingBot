package domain.updateService.model

data class NotifyFuture(
    val futureTicker: String,
    val futureName: String,
    val futurePrice: Double,
    val actualDifference: Double
)