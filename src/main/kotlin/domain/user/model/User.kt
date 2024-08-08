package domain.user.model

import java.time.LocalDateTime

data class User(
    val id: Long,
    val registered: LocalDateTime,
    val path: String,
    val defaultPercent: Double,
    val agentChatId: String?,
    val agentCode: String?,
    val agentNotifications: Boolean,
    val defaultRsiNotifications: Boolean,
    val defaultBBNotifications: Boolean,
)
