package data.db.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Shares : LongIdTable() {
    val uid = varchar("uid", 50)
    val ticker = varchar("ticker", 10)
    val name = varchar("name", 50)
    val lot = integer("lot")
}

class ShareEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ShareEntity>(Shares)

    var uid by Shares.uid
    var ticker by Shares.ticker
    var name by Shares.name
    var lot by Shares.lot
}