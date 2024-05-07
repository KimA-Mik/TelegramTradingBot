package data.db.entities

import domain.user.common.DEFAULT_SHARE_PERCENT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val id = long("id")
    override val primaryKey = PrimaryKey(id)
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val path = varchar("path", 250)
    val defaultPercent = double("default_percent").default(DEFAULT_SHARE_PERCENT)
    val agentChatId = varchar("agent_chat_id", 255).nullable().uniqueIndex()
    val agentCode = varchar("agent_code", 10).nullable()
    val agentNotifications = bool("agent_notifications").default(false)
}
