package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate

class RsiBbAlert(
    private val update: TelegramUpdate.RsiBbAlert
) : SecurityAlertScreen(update.user.id) {
    override val text
        get() = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        appendLine("*Сработал сигнал!* ")
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendPlannedPricesToSecurityAlert(update.security)

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice, renderMFI = true)
        appendNoteToSecurityAlert(update.security, hideProblematicUserNote)
    }

}