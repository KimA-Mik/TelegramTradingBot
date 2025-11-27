package presentation.telegram.settings.root.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.settings.root.callbackbutton.ToggleSrsiAlertCallbackButton

class SettingsRootScreen(private val user: User, messageId: Long? = null) : BotScreen(user.id, messageId) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = calculateReplayMarkup()
    private fun renderText() = buildString {
        appendLine("Текущие настройки:")
        appendLine("Уведомления SRSI: *${boolStatus(user.enableSrsi)}*")
    }

    private fun calculateReplayMarkup() = InlineKeyboardMarkup.create(
        listOf(
            ToggleSrsiAlertCallbackButton.getCallbackData(!user.enableSrsi),
        )
    )

    private fun boolStatus(value: Boolean) = if (value) "Включены" else "Выключены"
}