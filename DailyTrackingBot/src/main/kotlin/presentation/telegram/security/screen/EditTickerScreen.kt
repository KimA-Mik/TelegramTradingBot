package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.screen.BotScreen

class EditTickerScreen(userId: Long) : BotScreen(userId) {
    override val text = "Введите тикер бумаги"
    override val replyMarkup = _replyMarkup

    enum class Commands(val text: String) {
        Cancel("Отмена")
    }

    companion object {
        private val _replyMarkup = KeyboardReplyMarkup(
            listOf(
                buildList {
                    Commands.entries.forEach { KeyboardButton(it.text) }
                }
            )
        )
    }
}