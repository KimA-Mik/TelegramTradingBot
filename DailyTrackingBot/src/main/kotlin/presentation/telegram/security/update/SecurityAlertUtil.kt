package presentation.telegram.security.update

import domain.user.model.TrackingSecurity
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.util.TelegramUtil
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun StringBuilder.appendNoteToSecurityAlert(security: TrackingSecurity) {
    security.note?.takeIf { it.isNotBlank() }?.let {
        append("Заметка")
        security.noteUpdatedMs?.let {
            val ldt = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
            append("( ")
            append(ldt.format(TelegramUtil.localDateTimeFormat))
            append(')')
        }
        appendLine(":")
        append(security.note)
    }
}