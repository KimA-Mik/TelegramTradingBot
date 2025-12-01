package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.util.MathUtil
import presentation.telegram.core.screen.BotScreen
import presentation.util.PresentationUtil

class SrsiAlertScreen(
    private val update: TelegramUpdate.SrsiAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        appendLine("*Сработал сигнал по RSI!*")
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendPlannedPricesToSecurityAlert(update.security)
        appendLine("Критические значения SRSI:")
        for (interval in update.intervals) {
            when (interval) {
                TelegramUpdate.SrsiAlert.SrsiInterval.MIN15 -> {
                    val color = PresentationUtil.rsiColor(
                        update.indicators.min15Rsi,
                        MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH
                    )
                    append("*${color}SRSI (15м):* ${update.indicators.min15Rsi.formatToRu()}")
                }

                TelegramUpdate.SrsiAlert.SrsiInterval.HOUR4 -> {
                    val color = PresentationUtil.rsiColor(
                        update.indicators.hour4Rsi,
                        MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH
                    )
                    appendLine("*${color}SRSI (4ч):* ${update.indicators.hour4Rsi.formatToRu()}")
                }
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice, renderSrsi = true)
        appendNoteToSecurityAlert(update.security)
    }
}