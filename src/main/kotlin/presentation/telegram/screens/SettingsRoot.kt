package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.user.model.User
import presentation.common.formatAndTrim
import presentation.telegram.BotTextCommands

class SettingsRoot(user: User) : BotScreen(user.id) {
    override val text = "Текущие настрокйи:\n" +
            "НАчальный процент: ${user.defaultPercent.formatAndTrim(2)}%"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            KeyboardButton(BotTextCommands.Root.text),
            resizeKeyboard = true
        )
    }
}