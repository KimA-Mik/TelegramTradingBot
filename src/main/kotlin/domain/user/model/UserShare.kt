package domain.user.model

data class UserShare(
    val id: Long,
    val ticker: String,
    val name: String,
    val percent: Double
)
