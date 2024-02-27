package data.db.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserSecurities : LongIdTable() {
    val percent = double("percent")
    val user = reference("user", Users)
    val security = reference("security", Securities)
}

class UserSecurity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSecurity>(UserSecurities)

    val percent by UserSecurities.percent
    val user by User referencedOn UserSecurities.user
    val security by Security referencedOn UserSecurities.security
}