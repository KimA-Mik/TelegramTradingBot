package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.user.model.SecurityType
import presentation.telegram.core.screen.BotScreen
import presentation.util.PresentationUtil
import presentation.util.TelegramUtil

class RsiAlertScreen(
    private val update: TelegramUpdate.RsiAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun renderText() = buildString {
        appendLine("*Сработал сигнал по RSI(15м)!*")
        appendLine()
        when (update.security.type) {
            SecurityType.FUTURE -> appendLine("*Фьючерс:* ${TelegramUtil.clickableTrackingSecurity(update.security)} — (${update.security.name})")
            SecurityType.SHARE -> appendLine("*Акция:* ${TelegramUtil.clickableTrackingSecurity(update.security)} — (${update.security.name})")
        }

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}$ROUBLE_SIGN")
        appendLine("Критические значения RSI:")
        for (interval in update.intervals) {
            when (interval) {
                TelegramUpdate.RsiAlert.RsiInterval.MIN15 -> appendLine("*${PresentationUtil.rsiColor(update.indicators.min15Rsi)}RSI (15м):* ${update.indicators.min15Rsi.formatToRu()}")
                TelegramUpdate.RsiAlert.RsiInterval.HOUR4 -> appendLine("*${PresentationUtil.rsiColor(update.indicators.hour4Rsi)}RSI (4ч):* ${update.indicators.hour4Rsi.formatToRu()}")
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice)
        appendNoteToSecurityAlert(update.security)
    }
}