package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import domain.user.model.User
import presentation.common.formatAndTrim
import presentation.telegram.BotTextCommands
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.stateToText
import presentation.telegram.settings.textModels.SettingsTextModel

class SettingsRoot(user: User) : BotScreen(user.id) {
    override val text = generateText(user)
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    private fun generateText(user: User): String {
        var res = "Текущие настрокйи:\n" +
                "Начальный процент: ${user.defaultPercent.formatAndTrim(2)}%\n"

        val login = user.agentChatId ?: "не определён"
        res += "Ваш логин для Agent: $login\n"

        val enabled = stateToText(user.agentNotifications)
        res += "Уведомления через Agent: $enabled\n"

        res += "\nУведомления индикаторов по умолчаню:\n"
        res += "RSI: ${stateToText(user.defaultRsiNotifications)}\n"
        res += "Полосы Боллинджера: ${stateToText(user.defaultBbNotifications)}\n"

        return res
    }

    companion object {
        private val _replayMarkup = KeyboardReplyMarkup(
            listOf(
                listOf(
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.DefaultPercent.text),
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.ResetPercent.text)
                ),
                listOf(
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.AgentSettings.text),
                    KeyboardButton(SettingsTextModel.SettingsTextCommands.IndicatorsSettings.text)
                ),
                listOf(KeyboardButton(BotTextCommands.Root.text))
            ),
            resizeKeyboard = true
        )
    }
}