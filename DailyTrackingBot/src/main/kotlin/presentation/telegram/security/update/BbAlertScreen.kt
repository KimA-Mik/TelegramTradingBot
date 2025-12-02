package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import presentation.telegram.core.screen.BotScreen
import presentation.util.PresentationUtil

class BbAlertScreen(
    private val update: TelegramUpdate.BbAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        append("*Сработал сигнал по BB!* ")
        alertColor(update)?.let { append(it) }
        appendLine()
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendPlannedPricesToSecurityAlert(update.security)
        appendLine("Критические значения BB:")
        for (interval in update.intervals) {
            when (interval) {
                TelegramUpdate.BbAlert.BbInterval.MIN15 -> renderBb(
                    update.indicators.min15bb,
                    update.currentPrice,
                    "15м"
                )

                TelegramUpdate.BbAlert.BbInterval.HOUR4 -> renderBb(
                    update.indicators.hour4Bb,
                    update.currentPrice,
                    "4ч"
                )
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice, renderMFI = true)
        appendNoteToSecurityAlert(update.security)
    }

    private fun alertColor(update: TelegramUpdate.BbAlert) = update.intervals.firstOrNull()?.let {
        val bb = when (it) {
            TelegramUpdate.BbAlert.BbInterval.MIN15 -> update.indicators.min15bb
            TelegramUpdate.BbAlert.BbInterval.HOUR4 -> update.indicators.hour4Bb
        }
        PresentationUtil.markupBbColor(update.currentPrice, bb.lower, bb.upper)
    }
}