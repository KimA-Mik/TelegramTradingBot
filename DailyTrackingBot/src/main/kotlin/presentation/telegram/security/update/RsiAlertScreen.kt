package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import presentation.util.PresentationUtil

class RsiAlertScreen(
    private val update: TelegramUpdate.RsiAlert
) : SecurityAlertScreen(update.user.id) {
    override val text
        get() = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        append("*Сработал сигнал по RSI!* ")
        alertColor(update)?.let { append(it) }
        appendLine()
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendPlannedPricesToSecurityAlert(update.security)
        appendLine("Критические значения RSI:")
        for (interval in update.intervals) {
            when (interval) {
                TelegramUpdate.RsiAlert.RsiInterval.MIN15 -> appendLine("*${PresentationUtil.rsiColor(update.indicators.min15Rsi)}RSI (15м):* ${update.indicators.min15Rsi.formatToRu()}")
                TelegramUpdate.RsiAlert.RsiInterval.HOUR -> appendLine("*${PresentationUtil.rsiColor(update.indicators.hourlyRsi)}RSI (1ч):* ${update.indicators.hourlyRsi.formatToRu()}")
                TelegramUpdate.RsiAlert.RsiInterval.HOUR4 -> appendLine("*${PresentationUtil.rsiColor(update.indicators.hour4Rsi)}RSI (4ч):* ${update.indicators.hour4Rsi.formatToRu()}")
                TelegramUpdate.RsiAlert.RsiInterval.DAY -> appendLine("*${PresentationUtil.rsiColor(update.indicators.dailyRsi)}RSI (день):* ${update.indicators.dailyRsi.formatToRu()}")
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice)
        appendNoteToSecurityAlert(update.security, hideProblematicUserNote)
    }

    private fun alertColor(update: TelegramUpdate.RsiAlert) = update.intervals.firstOrNull()?.let {
        val rsi = when (it) {
            TelegramUpdate.RsiAlert.RsiInterval.MIN15 -> update.indicators.min15Rsi
            TelegramUpdate.RsiAlert.RsiInterval.HOUR -> update.indicators.hourlyRsi
            TelegramUpdate.RsiAlert.RsiInterval.HOUR4 -> update.indicators.hour4Rsi
            TelegramUpdate.RsiAlert.RsiInterval.DAY -> update.indicators.dailyRsi
        }
        PresentationUtil.rsiColor(rsi)
    }
}