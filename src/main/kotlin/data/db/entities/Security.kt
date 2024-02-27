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

class Security(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Security>(Securities)

    val uid by Securities.uid
    val ticker by Securities.ticker
    val name by Securities.name
    val lot by Securities.lot
}