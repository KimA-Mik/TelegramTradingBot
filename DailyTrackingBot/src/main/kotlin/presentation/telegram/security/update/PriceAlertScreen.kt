package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.user.model.SecurityType
import presentation.telegram.core.screen.BotScreen
import presentation.util.PresentationUtil
import presentation.util.TelegramUtil

class PriceAlertScreen(
    private val update: TelegramUpdate.PriceAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    private fun renderText() = buildString {
        appendLine("*Сработал ценовой сигнал!*")
        appendLine()
        when (update.security.type) {
            SecurityType.SHARE -> appendLine("*Акция:* ${TelegramUtil.clickableTrackingSecurity(update.security)} — (${update.security.name})")
            SecurityType.FUTURE -> appendLine("*Фьючерс:* ${TelegramUtil.clickableTrackingSecurity(update.security)} — (${update.security.name})")
        }
        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}${ROUBLE_SIGN}")
        appendLine("*Целевая цена:* ${update.security.targetPrice.formatToRu()}${ROUBLE_SIGN}")
        appendLine("*Отклонение:* ${update.currentDeviation.formatToRu()}%")
        update.indicators?.let { ind ->
            appendLine()
            appendLine("*Индикаторы:*")
            appendLine("*RSI (15м):* ${PresentationUtil.renderRsi(ind.min15Rsi)}")
            appendLine("*RSI (1ч):* ${PresentationUtil.renderRsi(ind.hourlyRsi)}")
            appendLine("*RSI (1д):* ${PresentationUtil.renderRsi(ind.dailyRsi)}")
            var bbColor = PresentationUtil.markupBbColor(ind.min15bb.middle, ind.min15bb.lower, ind.min15bb.upper)
            append("*BB (15м) $bbColor:* верхняя: ${ind.min15bb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.min15bb.lower.formatToRu()}")
            bbColor = PresentationUtil.markupBbColor(ind.hourlyBb.middle, ind.hourlyBb.lower, ind.hourlyBb.upper)
            append("*BB (1ч) $bbColor:* верхняя: ${ind.hourlyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.hourlyBb.lower.formatToRu()}")
            bbColor = PresentationUtil.markupBbColor(ind.dailyBb.middle, ind.dailyBb.lower, ind.dailyBb.upper)
            append("*BB (1д) $bbColor:* верхняя: ${ind.dailyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.dailyBb.lower.formatToRu()}")
        }
        update.security.note?.takeIf { it.isNotBlank() }?.let {
            appendLine()
            append("Заметка: ")
            append(it)
        }
    }
}