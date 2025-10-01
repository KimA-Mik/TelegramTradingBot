package data.db.tables

import domain.common.MAX_NOTE_LENGTH
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Users : LongIdTable("users") {
    val path = varchar(name = "path", length = 256).default("")
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val ticker = varchar(name = "ticker", length = 32).nullable().default(null)
    val targetPrice = double(name = "target_rice").nullable().default(null)
    val targetDeviations = double(name = "target_deviations").nullable().default(null)
    val isActive = bool(name = "is_active").default(false)
    val remainActive = bool(name = "remain_active").default(false)
    val securityConfigureSequence = bool(name = "security_configure_sequence").default(false)
    val note = varchar(name = "note", length = MAX_NOTE_LENGTH).nullable().default(null)
    val showNote = bool(name = "show_note").default(true)
}