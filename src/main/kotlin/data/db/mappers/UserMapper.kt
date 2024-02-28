package data.db.mappers

import data.db.entities.UserEntity
import domain.user.model.User

fun UserEntity.toUser(): User = User(
    id = id.value,
    registered = registered,
    path = path
)