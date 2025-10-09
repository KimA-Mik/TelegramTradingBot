package presentation.telegram.security.list.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class SecurityHeader(id: Long) : BotScreen(id) {
    override val text = "Ваши бумаги:"
    override val replyMarkup = _replayMarkup

    enum class Command(val text: String) {
        Notes("Редактировать заметку"),
        Price("Редактировать желаемую цену"),
        Percent("Редактировать процент расхождения"),
    }

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            buildList {
                Command.entries.forEach {
                    add(
                        listOf(KeyboardButton(it.text))
                    )
                }
                add(
                    listOf(KeyboardButton(DefaultCommands.Pop.text))
                )
            },
            resizeKeyboard = true
        )
    }
}