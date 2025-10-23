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
        appendLine("*${PresentationUtil.rsiColor(update.currentRsi)}Текущий RSI:* ${update.currentRsi.formatToRu()}")
        update.indicators?.let { ind ->
            appendLine()
            appendLine("*Индикаторы:*")
            appendLine("*${PresentationUtil.rsiColor(ind.hourlyRsi)}RSI (1ч):* ${ind.hourlyRsi.formatToRu()}")
            appendLine("*${PresentationUtil.rsiColor(ind.dailyRsi)}RSI (1д):* ${ind.dailyRsi.formatToRu()}")
            var bbColor = PresentationUtil.markupBbColor(update.currentPrice, ind.min15bb.lower, ind.min15bb.upper)
            append("*${bbColor}BB (15м):* ${ind.min15bb.lower.formatToRu()} - ")
            append("*${ind.min15bb.middle.formatToRu()}* - ")
            appendLine(ind.min15bb.upper.formatToRu())
            bbColor = PresentationUtil.markupBbColor(update.currentPrice, ind.hourlyBb.lower, ind.hourlyBb.upper)
            append("*${bbColor}BB (1ч):* ${ind.hourlyBb.lower.formatToRu()} - ")
            append("*${ind.hourlyBb.middle.formatToRu()}* - ")
            appendLine(ind.hourlyBb.upper.formatToRu())
            bbColor = PresentationUtil.markupBbColor(update.currentPrice, ind.dailyBb.lower, ind.dailyBb.upper)
            append("*${bbColor}BB (1д):* ${ind.dailyBb.lower.formatToRu()} - ")
            append("*${ind.dailyBb.middle.formatToRu()}* - ")
            appendLine(ind.dailyBb.upper.formatToRu())
        }

        appendNoteToSecurityAlert(update.security)
    }
}