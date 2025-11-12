package presentation.telegram.security.edit.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.common.MAX_NOTE_LENGTH
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen
import ru.kima.telegrambot.common.util.PresentationUtil

class EditNoteScreen(
    userId: Long,
    private val previousNote: String?,
    private val previousNoteUpdated: Long?
) : BotScreen(userId) {
    override val text = renderText()
    override val replyMarkup = _replayMarkup

    enum class Commands(val text: String) {
        Delete("Удалить заметку")
    }

    private fun renderText() = buildString {
        previousNote?.let {
            append("Предыдущая заметка")
            previousNoteUpdated?.let { timestamp ->
                append(" (")
                append(PresentationUtil.renderLongTimestamp(timestamp))
                append(')')
            }
            appendLine(':')
            appendLine(it)
        }
        append("\nВведите новую заметку для этой ценной бумаги. Заметка может содержать до $MAX_NOTE_LENGTH символов.")
    }

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(Commands.Delete.text)),
                listOf(KeyboardButton(DefaultCommands.Pop.text))
            ),
            resizeKeyboard = true
        )
    }
}