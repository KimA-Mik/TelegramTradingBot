package data.db.entities

import org.jetbrains.exposed.dao.id.LongIdTable

object UserShares : LongIdTable() {
    val percent = double("percent")
    val userId = long("userId")
    val shareId = long("shareId")
    val notified = bool("notified")
}