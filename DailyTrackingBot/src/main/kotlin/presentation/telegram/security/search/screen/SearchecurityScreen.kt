package presentation.telegram.security.search.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class SearchecurityScreen(userId: Long) : BotScreen(userId) {
    override val text = "Введите тикер бумаги"
    override val replyMarkup = _replyMarkup

    companion object {
        private val _replyMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(DefaultCommands.Root.text))
            ),
            resizeKeyboard = true
        )
    }
}