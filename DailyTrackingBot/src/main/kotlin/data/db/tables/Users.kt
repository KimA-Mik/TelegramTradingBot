package data.db.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.CurrentDateTime
import org.jetbrains.exposed.v1.javatime.datetime

object Users : LongIdTable("users") {
    val path = varchar(name = "path", length = 256).default("")
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
}