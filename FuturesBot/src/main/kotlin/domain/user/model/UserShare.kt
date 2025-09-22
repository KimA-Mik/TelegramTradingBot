package domain.user.model

data class UserShare(
    val id: Long,
    val uid: String,
    val ticker: String,
    val name: String,
    val percent: Double,
    val futuresNotified: Boolean,
    val rsiNotified: Boolean,
    val bollingerBandsNotified: Boolean,
    val rsiNotificationsEnabled: Boolean,
    val bbNotificationsEnabled: Boolean,
)
