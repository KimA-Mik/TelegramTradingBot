package domain.user.model

data class FullUser(
    val user: User,
    val securities: List<TrackingSecurity>
)
