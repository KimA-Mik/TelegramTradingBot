package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.screen.BotScreen

class SecurityHeader(id: Long) : BotScreen(id) {
    override val text = "Ваша бумага:"
    override val replyMarkup = _replayMarkup

    enum class Command(val text: String) {
        Reconfigure("Настроить все"),
        Ticker("Тикер"),
        Price("Ожидаемая цена"),
        Percent("Процент"),
        Notes("Заметка")
    }

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(Command.Reconfigure.text)),
                listOf(
                    KeyboardButton(Command.Ticker.text),
                    KeyboardButton(Command.Price.text),
                    KeyboardButton(Command.Percent.text)
                ),
                listOf(KeyboardButton(Command.Notes.text)),
            )
        )
    }
}