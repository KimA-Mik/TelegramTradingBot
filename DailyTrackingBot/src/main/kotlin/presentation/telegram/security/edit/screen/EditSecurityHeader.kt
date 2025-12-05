package presentation.telegram.security.edit.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class EditSecurityHeader(id: Long) : BotScreen(id) {
    override val text = "Ваша бумага:"
    override val replyMarkup = _replayMarkup

    enum class Command(val text: String) {
        Notes("Редактировать заметку"),
        Price("Цена продажи"),
        LowPrice("Цена покупки"),
        Percent("Редактировать процент расхождения"),
    }

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(Command.Notes.text)),
                listOf(KeyboardButton(Command.Price.text), KeyboardButton(Command.LowPrice.text)),
                listOf(KeyboardButton(Command.Percent.text)),
                listOf(KeyboardButton(DefaultCommands.Pop.text))
            ),
            resizeKeyboard = true
        )
    }
}