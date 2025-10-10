package data.db.entities

import data.db.tables.Securities
import data.db.tables.Users
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var path by Users.path
    var registered by Users.registered

    val securities by SecurityEntity referrersOn Securities.user

    override fun toString(): String {
        return "User(id=$id, path=$path, registered=$registered, securities=${securities.toList()})"
    }
}