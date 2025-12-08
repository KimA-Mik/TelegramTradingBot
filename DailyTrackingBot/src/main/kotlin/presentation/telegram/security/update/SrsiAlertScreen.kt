package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.util.MathUtil
import presentation.util.PresentationUtil

class SrsiAlertScreen(
    private val update: TelegramUpdate.SrsiAlert
) : SecurityAlertScreen(update.user.id) {
    override val text
        get() = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        append("*Сработал сигнал по SRSI!* ")
        alertColor(update)?.let { append(it) }
        appendLine()
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendPlannedPricesToSecurityAlert(update.security)
        appendLine("Критические значения SRSI:")
        for (interval in update.intervals) {
            when (interval) {
                TelegramUpdate.SrsiAlert.SrsiInterval.MIN15 -> {
                    val color = PresentationUtil.rsiColor(
                        update.indicators.min15Srsi,
                        MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH
                    )
                    appendLine("*${color}SRSI (15м):* ${update.indicators.min15Srsi.formatToRu()}")
                }

                TelegramUpdate.SrsiAlert.SrsiInterval.HOUR4 -> {
                    val color = PresentationUtil.rsiColor(
                        update.indicators.hour4Srsi,
                        MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH
                    )
                    appendLine("*${color}SRSI (4ч):* ${update.indicators.hour4Srsi.formatToRu()}")
                }
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice, renderSrsi = true)
        appendNoteToSecurityAlert(update.security, hideProblematicUserNote)
    }

    private fun alertColor(update: TelegramUpdate.SrsiAlert): String? = update.intervals.firstOrNull()?.let {
        val rsi = when (it) {
            TelegramUpdate.SrsiAlert.SrsiInterval.MIN15 -> update.indicators.min15Srsi
            TelegramUpdate.SrsiAlert.SrsiInterval.HOUR4 -> update.indicators.hour4Srsi
        }
        PresentationUtil.rsiColor(rsi, MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH)
    }
}