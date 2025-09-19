package data.db.entities

import domain.user.common.DEFAULT_SHARE_PERCENT
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object Users : Table() {
    val id = long("id")
    override val primaryKey = PrimaryKey(id)
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val path = varchar("path", 250)
    val defaultPercent = double("default_percent").default(DEFAULT_SHARE_PERCENT)

    val defaultRsiNotifications = bool("default_rsi_notifications").default(true)
    val defaultBbNotifications = bool("default_bb_notifications").default(false)
}
