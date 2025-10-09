package data.db.tables

import domain.common.MAX_NOTE_LENGTH
import domain.user.model.SecurityType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Securities : LongIdTable("security") {
    val user = reference("user_id", Users)
    val ticker = varchar(name = "ticker", length = 32)
    val name = varchar(name = "name", length = 128)
    val uid = varchar(name = "uid", length = 32)
    val targetPrice = double(name = "target_rice")
    val targetDeviations = double(name = "target_deviations")
    val isActive = bool(name = "is_active").default(false)
    val remainActive = bool(name = "remain_active").default(false)
    val note = varchar(name = "note", length = MAX_NOTE_LENGTH).nullable().default(null)
    val showNote = bool(name = "show_note").default(true)
    val shouldNotify = bool(name = "should_notify").default(true)
    val shouldNotifyRsi = bool(name = "should_notify_rsi").default(true)
    val type = enumerationByName(name = "type", length = 16, klass = SecurityType::class)
}

