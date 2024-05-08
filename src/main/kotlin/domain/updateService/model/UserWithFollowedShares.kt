package domain.updateService.model

import domain.user.model.UserShare

data class UserWithFollowedShares(
    val id: Long,
    val agentChatId: String?,
    val agentNotifications: Boolean,
    val shares: List<UserShare> = emptyList()
)