package domain.updateService.model

data class NotifyShare(
    val shareTicker: String,
    val sharePrice: Double,
    val minimalDifference: Double,
    val futures: List<NotifyFuture>
)
