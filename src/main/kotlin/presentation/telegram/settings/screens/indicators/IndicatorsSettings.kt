package presentation.telegram.settings.screens.indicators

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.User
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.callbackButtons.indicators.bollingerBands.ResetBbDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.bollingerBands.SwitchBbDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.rsi.ResetRsiDefaultCallbackButton
import presentation.telegram.settings.callbackButtons.indicators.rsi.SwitchRsiDefaultCallbackButton

class IndicatorsSettings(user: User, messageId: Long? = null) : BotScreen(user.id, messageId) {
    override val text = generateText(user)
    override val replyMarkup = calculateReplayMarkup()

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

    private fun calculateReplayMarkup(): ReplyMarkup {
        return InlineKeyboardMarkup.create(
            listOf(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        SwitchRsiDefaultCallbackButton.text,
                        SwitchRsiDefaultCallbackButton.callbackData
                    ),
                    InlineKeyboardButton.CallbackData(
                        ResetRsiDefaultCallbackButton.text,
                        ResetRsiDefaultCallbackButton.callbackData
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        SwitchBbDefaultCallbackButton.text,
                        SwitchBbDefaultCallbackButton.callbackData
                    ),
                    InlineKeyboardButton.CallbackData(
                        ResetBbDefaultCallbackButton.text,
                        ResetBbDefaultCallbackButton.callbackData
                    )
                )
            )
        )
    }
}