package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import presentation.telegram.BotTextCommands

class MySecurities(id: Long) : BotScreen(id) {
    override val text = "Пока тут не на что смотреть"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )
    }
}
