package domain.user.model

data class TrackingSecurity(
    val id: Long,
    val ticker: String,
    val uid: String,
    val targetPrice: Double?,
    val targetDeviation: Double?,
    val isActive: Boolean,
    val remainActive: Boolean,
    val note: String?,
    val showNote: Boolean,
    val shouldNotify: Boolean,
    val shouldNotifyRsi: Boolean
)