package data.db.entities

import domain.user.common.DEFAULT_SHARE_PERCENT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val id = long("id")
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val path = varchar("path", 250)
    val defaultPercent = double("default_percent").default(DEFAULT_SHARE_PERCENT)
    override val primaryKey = PrimaryKey(id)
}
