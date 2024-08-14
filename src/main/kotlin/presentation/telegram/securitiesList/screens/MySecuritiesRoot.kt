package presentation.telegram.securitiesList.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.BotTextCommands
import presentation.telegram.screens.BotScreen

class MySecuritiesRoot(id: Long) : BotScreen(id) {
    override val text = "Ваши акции"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )
    }
}
