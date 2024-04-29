package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.user.model.User
import presentation.common.formatAndTrim
import presentation.telegram.BotTextCommands
import presentation.telegram.textModels.SettingsTextModel

class SettingsRoot(user: User) : BotScreen(user.id) {
    override val text = "Текущие настрокйи:\n" +
            "Начальный процент: ${user.defaultPercent.formatAndTrim(2)}%"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.DefaultPercent.text),
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.ResetPercent.text)
                ),
                listOf(KeyboardButton(BotTextCommands.Root.text))
            ),
            resizeKeyboard = true
        )
    }
}