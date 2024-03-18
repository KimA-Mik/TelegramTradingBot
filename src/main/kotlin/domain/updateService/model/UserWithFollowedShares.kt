package domain.updateService.model

data class UserWithFollowedShares(
    val id: Long,
    val shares: List<FollowedShare> = emptyList()
)