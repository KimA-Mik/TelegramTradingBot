package data.db.entities

import data.db.tables.Securities
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class SecurityEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SecurityEntity>(Securities)

    var user by UserEntity referencedOn Securities.user
    var ticker by Securities.ticker
    var name by Securities.name
    var uid by Securities.uid
    var targetPrice by Securities.targetPrice
    var targetDeviation by Securities.targetDeviations
    var isActive by Securities.isActive
    var remainActive by Securities.remainActive
    var note by Securities.note
    var showNote by Securities.showNote
    var shouldNotify by Securities.shouldNotify
    var shouldNotifyRsi by Securities.shouldNotifyRsi
    var type by Securities.type

    override fun toString(): String {
        return "Security(id=$id, ticker=$ticker, uid=$uid, targetPrice=$targetPrice, targetDeviation=$targetDeviation, isActive=$isActive, remainActive=$remainActive, note=$note)"
    }
}
