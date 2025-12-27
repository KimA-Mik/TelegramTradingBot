package presentation.telegram.settings.root.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.user.model.User
import domain.user.usecase.ChangeTimeframesToFireUseCase
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.settings.root.callbackbutton.ChangeTimeframesToFireCallbackButton
import presentation.telegram.settings.root.callbackbutton.ToggleSrsiAlertCallbackButton

class SettingsRootScreen(private val user: User, messageId: Long? = null) : BotScreen(user.id, messageId) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = calculateReplayMarkup()
    private fun renderText() = buildString {
        appendLine("Текущие настройки:")
        appendLine("Уведомления SRSI: *${boolStatus(user.enableSrsi)}*")
        appendLine("Таймфреймов для уведомления: *${user.timeframesToFire}*")
    }

    private fun calculateReplayMarkup() = InlineKeyboardMarkup.create(
        listOf(
            listOf(
                ToggleSrsiAlertCallbackButton.getCallbackData(!user.enableSrsi),
            ),
            listOf(
                ChangeTimeframesToFireCallbackButton.getCallbackData(ChangeTimeframesToFireUseCase.Direction.DECREASE),
                ChangeTimeframesToFireCallbackButton.getCallbackData(ChangeTimeframesToFireUseCase.Direction.INCREASE)
            )
        )
    )

    private fun boolStatus(value: Boolean) = if (value) "Включены" else "Выключены"
}