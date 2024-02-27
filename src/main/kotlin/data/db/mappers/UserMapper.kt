package data.db.mappers

import data.db.entities.UserEntity
import domain.local.model.User

fun UserEntity.toUser(): User = User(
    id = id.value,
    registered = registered,
    path = path
)