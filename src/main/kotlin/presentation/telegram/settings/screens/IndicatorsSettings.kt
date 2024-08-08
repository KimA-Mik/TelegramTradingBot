package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.User
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.callbackButtons.indicators.ResetBbDefault
import presentation.telegram.settings.callbackButtons.indicators.ResetRsiDefault
import presentation.telegram.settings.callbackButtons.indicators.SwitchBbDefault
import presentation.telegram.settings.callbackButtons.indicators.SwitchRsiDefault

class IndicatorsSettings(user: User, messageId: Long? = null) : BotScreen(user.id, messageId) {
    override val text = generateText(user)
    override val replyMarkup = calculateReplayMarkup(user)
    override val parseMode = null

    private fun generateText(user: User): String {
        var res = "Уведомления индикаторов по умолчаню:\n"
        res += "RSI: ${notificationsStateText(user.defaultRsiNotifications)}\n"
        res += "Полосы Боллинджера: ${notificationsStateText(user.defaultBbNotifications)}\n"

        return res
    }

    private fun notificationsStateText(state: Boolean): String {
        return when (state) {
            true -> "включены"
            false -> "выключены"
        }
    }

    private fun calculateReplayMarkup(user: User): ReplyMarkup {
        return InlineKeyboardMarkup.create(
            listOf(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        SwitchRsiDefault.getText(user.defaultRsiNotifications),
                        SwitchRsiDefault.callbackData
                    ),
                    InlineKeyboardButton.CallbackData(
                        ResetRsiDefault.text,
                        ResetRsiDefault.callbackData
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        SwitchBbDefault.getText(user.defaultBbNotifications),
                        SwitchBbDefault.callbackData
                    ),
                    InlineKeyboardButton.CallbackData(
                        ResetBbDefault.text,
                        ResetBbDefault.callbackData
                    )
                )
            )
        )
    }
}