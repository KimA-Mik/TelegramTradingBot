package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.BotTextCommands

class Error(id: Long, message: String) : BotScreen(id) {
    override val text = "Произошла ошибка: $message"
    override val replyMarkup = _replyMarkup
    override val parseMode = null

    companion object {
        private val _replyMarkup = KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )
    }
}
