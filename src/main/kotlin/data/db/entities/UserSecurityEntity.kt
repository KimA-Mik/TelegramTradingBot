package data.db.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserSecurities : LongIdTable() {
    val percent = double("percent")
    val user = reference("user", Users)
    val security = reference("security", Shares)
}

class UserSecurityEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSecurityEntity>(UserSecurities)

    var percent by UserSecurities.percent
    var userEntity by UserEntity referencedOn UserSecurities.user
    var shareEntity by ShareEntity referencedOn UserSecurities.security
}