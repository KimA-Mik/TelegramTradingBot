package data.db.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val id = long("id")
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val path = varchar("path", 250)
    override val primaryKey = PrimaryKey(id)
}
