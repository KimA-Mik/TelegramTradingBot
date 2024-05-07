package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.common.formatAndTrim
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.screens.BotScreen

class SettingsDefaultPercent(userId: Long, percent: Double, messageId: Long? = null) : BotScreen(userId, messageId) {
    override val text = "Текущий стандартный процент: ${percent.formatAndTrim(2)}%"
    override val replyMarkup = _replayMarkup
    override val parseMode = null

    companion object {
        private val _replayMarkup = InlineKeyboardMarkup.create(
            listOf(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.EditDefaultPercent.getText(-1.0),
                        CallbackButton.EditDefaultPercent.getCallbackData(-1.0)
                    ),
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.EditDefaultPercent.getText(1.0),
                        CallbackButton.EditDefaultPercent.getCallbackData(1.0)
                    ),
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.EditDefaultPercent.getText(-0.1),
                        CallbackButton.EditDefaultPercent.getCallbackData(-0.1)
                    ),
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.EditDefaultPercent.getText(0.1),
                        CallbackButton.EditDefaultPercent.getCallbackData(0.1)
                    ),
                )
            )
        )
    }
}