package data.db.tables

import domain.common.MAX_NOTE_LENGTH
import domain.user.model.SecurityType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Securities : LongIdTable("security") {
    val user = reference("user_id", Users)
    val ticker = varchar(name = "ticker", length = 32)
    val name = varchar(name = "name", length = 128)
    val uid = varchar(name = "uid", length = 64)
    val targetPrice = double(name = "target_price").nullable().default(null)
    val lowTargetPrice = double(name = "low_target_price").nullable().default(null)
    val targetDeviations = double(name = "target_deviations")
    val isActive = bool(name = "is_active").default(false)
    val remainActive = bool(name = "remain_active").default(false)
    val note = varchar(name = "note", length = MAX_NOTE_LENGTH).nullable().default(null)
    val noteUpdated = datetime("note_updated").nullable().default(null)
    val showNote = bool(name = "show_note").default(true)
    val shouldNotify = bool(name = "should_notify").default(true)
    val shouldNotifyRsi = bool(name = "should_notify_rsi").default(true)
    val shouldNotifyBb = bool(name = "should_notify_bb").default(true)
    val type = enumerationByName(name = "type", length = 16, klass = SecurityType::class)
    val lastUnboundUpdate = long("last_unbound_update").default(0L)
}

