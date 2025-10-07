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
) {
    companion object {
        fun default(
            ticker: String,
            uid: String,
            id: Long = 0L,
            targetPrice: Double = 0.0,
            targetDeviation: Double = 0.1,
            isActive: Boolean = true,
            remainActive: Boolean = true,
            note: String? = null,
            showNote: Boolean = true,
            shouldNotify: Boolean = true,
            shouldNotifyRsi: Boolean = true
        ) = TrackingSecurity(
            id = id,
            ticker = ticker,
            uid = uid,
            targetPrice = targetPrice,
            targetDeviation = targetDeviation,
            isActive = isActive,
            remainActive = remainActive,
            note = note,
            showNote = showNote,
            shouldNotify = shouldNotify,
            shouldNotifyRsi = shouldNotifyRsi
        )
    }
}