package data.db.mappers

import data.db.entities.SecurityEntity
import domain.user.model.TrackingSecurity

fun SecurityEntity.toTrackingSecurity() = TrackingSecurity(
    id = id.value,
    ticker = ticker,
    name = name,
    uid = uid,
    targetPrice = targetPrice,
    lowTargetPrice = lowTargetPrice,
    targetDeviation = targetDeviation,
    isActive = isActive,
    remainActive = remainActive,
    note = note,
    showNote = showNote,
    shouldNotify = shouldNotify,
    shouldNotifyRsi = shouldNotifyRsi,
    type = type
)