package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class EditTickerScreen(userId: Long) : BotScreen(userId) {
    override val text = "Введите тикер бумаги"
    override val replyMarkup = _replyMarkup

    companion object {
        private val _replyMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(DefaultCommands.Pop.text))
            ),
            resizeKeyboard = true
        )
    }
}