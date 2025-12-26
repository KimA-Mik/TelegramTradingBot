package data.db.mappers

import data.db.entities.UserEntity
import domain.user.model.FullUser
import domain.user.model.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun UserEntity.toUser() = User(
    id = id.value,
    path = path,
    registered = registered.toInstant(TimeZone.currentSystemDefault()),
    defaultPriceProlongation = defaultPriceProlongation,
    enableSrsi = enableSrsi,
    timeframesToFire = timeframesToFire,
)

fun UserEntity.toFullUser() = FullUser(
    user = toUser(),
    securities = securities.map { it.toTrackingSecurity() }
)