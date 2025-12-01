package data.db.mappers

import data.db.entities.SecurityEntity
import domain.user.model.TrackingSecurity
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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
    noteUpdatedMs = noteUpdated?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
    showNote = showNote,
    shouldNotify = shouldNotify,
    shouldNotifyRsi = shouldNotifyRsi,
    shouldNotifyBb = shouldNotifyBb,
    shouldNotifySrsi = shouldNotifySrsi,
    type = type,
    lastUnboundUpdateSec = lastUnboundUpdate
)