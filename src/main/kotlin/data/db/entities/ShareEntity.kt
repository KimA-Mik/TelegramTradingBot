package data.db.entities

import org.jetbrains.exposed.dao.id.LongIdTable

object Shares : LongIdTable() {
    val uid = varchar("uid", 50)
    val ticker = varchar("ticker", 10).uniqueIndex()
    val name = varchar("name", 50)
    val lot = integer("lot")
}