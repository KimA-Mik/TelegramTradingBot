package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.util.MathUtil
import presentation.telegram.core.screen.BotScreen

class BbAlertScreen(
    private val update: TelegramUpdate.BbAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        appendLine("*Сработал сигнал по BB!*")
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
        appendIndicatorsToSecurityAlert(
            update.indicators,
            update.currentPrice,
            rsiLow = MathUtil.RSI_FOR_BB_LOW,
            rsiHigh = MathUtil.RSI_FOR_BB_HIGH
        )
        appendNoteToSecurityAlert(update.security)
    }
}