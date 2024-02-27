package data.db.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Securities : LongIdTable() {
    val uid = varchar("uid", 50)
    val ticker = varchar("ticker", 10)
    val name = varchar("name", 50)
    val lot = integer("lot")
}

class SecurityEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SecurityEntity>(Securities)

    var uid by Securities.uid
    var ticker by Securities.ticker
    var name by Securities.name
    var lot by Securities.lot
}