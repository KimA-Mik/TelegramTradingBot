package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.screen.BotScreen

class EditPriceScreen(userId: Long) : BotScreen(userId) {
    override val text = "Bведите ожидаемую цену"
    override val replyMarkup = _replayMarkup

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(KeyboardButton(DefaultCommands.Pop.text))
            ),
            resizeKeyboard = true
        )
    }
}