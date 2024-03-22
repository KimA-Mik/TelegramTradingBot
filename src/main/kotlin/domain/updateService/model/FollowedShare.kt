package domain.updateService.model

data class FollowedShare(
    val id: Long,
    val ticker: String,
    val uid: String,
    val percent: Double,
    val notified: Boolean
)