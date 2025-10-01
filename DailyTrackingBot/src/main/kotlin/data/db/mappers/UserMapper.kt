package data.db.mappers

import data.db.entities.UserEntity
import domain.user.model.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun UserEntity.toUser() = User(
    id = id.value,
    path = path,
    registered = registered.toInstant(TimeZone.currentSystemDefault()),
    ticker = ticker,
    targetPrice = targetPrice,
    targetDeviation = targetDeviation,
    isActive = isActive,
    remainActive = remainActive,
    note = note,
    showNote = showNote,
    securityConfigureSequence = securityConfigureSequence,
)
