package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.common.formatAndTrim
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.callbackButtons.EditDefaultPercentCallbackButton

class SettingsDefaultPercent(userId: Long, percent: Double, messageId: Long? = null) : BotScreen(userId, messageId) {
    override val text = "Текущий стандартный процент: ${percent.formatAndTrim(2)}%"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = InlineKeyboardMarkup.create(
            listOf(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        EditDefaultPercentCallbackButton.getText(-1.0),
                        EditDefaultPercentCallbackButton.getCallbackData(-1.0)
                    ),
                    InlineKeyboardButton.CallbackData(
                        EditDefaultPercentCallbackButton.getText(1.0),
                        EditDefaultPercentCallbackButton.getCallbackData(1.0)
                    ),
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        EditDefaultPercentCallbackButton.getText(-0.1),
                        EditDefaultPercentCallbackButton.getCallbackData(-0.1)
                    ),
                    InlineKeyboardButton.CallbackData(
                        EditDefaultPercentCallbackButton.getText(0.1),
                        EditDefaultPercentCallbackButton.getCallbackData(0.1)
                    ),
                )
            )
        )
    }
}