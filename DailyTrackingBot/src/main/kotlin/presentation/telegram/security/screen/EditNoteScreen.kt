package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.common.MAX_NOTE_LENGTH
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class EditNoteScreen(userId: Long) : BotScreen(userId) {
    override val text =
        "Введите заметку для этой ценной бумаги. Заметка может содержать до $MAX_NOTE_LENGTH символов."
    override val replyMarkup = _replayMarkup

    enum class Commands(val text: String) {
        Delete("Удалить заметку")
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