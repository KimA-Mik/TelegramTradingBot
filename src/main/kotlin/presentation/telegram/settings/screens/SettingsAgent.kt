package presentation.telegram.settings.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.User
import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.callbackButtons.DisableAgentNotificationsCallbackButton
import presentation.telegram.settings.callbackButtons.EnableAgentNotificationsCallbackButton
import presentation.telegram.settings.callbackButtons.LinkAgentAccountCallbackButton
import presentation.telegram.settings.callbackButtons.UnlinkAgentAccountCallbackButton

class SettingsAgent(user: User, messageId: Long? = null) : BotScreen(user.id, messageId) {
    override val text = "Настройки Agent"
    override val replyMarkup = calculateReplayMarkup(user)
    override val parseMode = null

    private fun calculateReplayMarkup(user: User): ReplyMarkup {
        val linkButton = if (user.agentChatId == null) {
            InlineKeyboardButton.CallbackData(
                LinkAgentAccountCallbackButton.text,
                LinkAgentAccountCallbackButton.getCallbackData(user.id)
            )
        } else {
            InlineKeyboardButton.CallbackData(
                UnlinkAgentAccountCallbackButton.text,
                UnlinkAgentAccountCallbackButton.getCallbackData(user.id)
            )
        }

        val notificationsButton = if (user.agentNotifications) {
            InlineKeyboardButton.CallbackData(
                DisableAgentNotificationsCallbackButton.text,
                DisableAgentNotificationsCallbackButton.callbackData
            )
        } else {
            InlineKeyboardButton.CallbackData(
                EnableAgentNotificationsCallbackButton.text,
                EnableAgentNotificationsCallbackButton.callbackData
            )
        }

        return InlineKeyboardMarkup.create(
            listOf(linkButton),
            listOf(notificationsButton)
        )
    }
}