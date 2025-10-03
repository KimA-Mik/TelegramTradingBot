package data.db.entities

import data.db.tables.Users
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var path by Users.path
    var registered by Users.registered
    var ticker by Users.ticker
    var targetPrice by Users.targetPrice
    var isActive by Users.isActive
    var remainActive by Users.remainActive
    var targetDeviation by Users.targetDeviations
    var securityConfigureSequence by Users.securityConfigureSequence
    var note by Users.note
    var showNote by Users.showNote
    var shouldNotify by Users.shouldNotify

    override fun toString(): String {
        return "User(id=$id, path=$path, registered=$registered, ticker=$ticker, targetPrice=$targetPrice, targetDeviation=$targetDeviation, isActive=$isActive, remainActive=$remainActive note=$note)"
    }
}