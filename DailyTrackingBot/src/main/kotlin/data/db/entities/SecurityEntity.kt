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
    var lowTargetPrice by Securities.lowTargetPrice
    var targetDeviation by Securities.targetDeviations
    var isActive by Securities.isActive
    var remainActive by Securities.remainActive
    var note by Securities.note
    var noteUpdated by Securities.noteUpdated
    var showNote by Securities.showNote
    var shouldNotify by Securities.shouldNotify
    var shouldNotifyRsi by Securities.shouldNotifyRsi
    var shouldNotifyBb by Securities.shouldNotifyBb
    var shouldNotifySrsi by Securities.shouldNotifySrsi
    var type by Securities.type
    var lastUnboundUpdate by Securities.lastUnboundUpdate

    override fun toString(): String {
        return "Security(id=$id, ticker=$ticker, name=$name, uid=$uid, targetPrice=$targetPrice, " +
                "lowTargetPrice=$lowTargetPrice, targetDeviation=$targetDeviation, isActive=$isActive, " +
                "remainActive=$remainActive, note=$note, noteUpdated=$noteUpdated, showNote=$showNote, " +
                "shouldNotify=$shouldNotify, shouldNotifyRsi=$shouldNotifyRsi, shouldNotifyBb=$shouldNotifyBb, " +
                "shouldNotifySrsi=$shouldNotifySrsi, type=$type, lastUnboundUpdate=$lastUnboundUpdate)"
    }
}
