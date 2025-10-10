package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.user.model.SecurityType
import presentation.telegram.core.screen.BotScreen
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
            appendLine("*RSI (15м):* ${ind.min15Rsi.formatToRu()}")
            appendLine("*RSI (1ч):* ${ind.hourlyRsi.formatToRu()}")
            appendLine("*RSI (1д):* ${ind.dailyRsi.formatToRu()}")
            append("*BB (15м):* верхняя: ${ind.min15bb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.min15bb.lower.formatToRu()}")
            append("*BB (1ч):* верхняя: ${ind.hourlyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.hourlyBb.lower.formatToRu()}")
            append("*BB (1д):* верхняя: ${ind.dailyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.dailyBb.lower.formatToRu()}")
        }
        update.security.note?.takeIf { it.isNotBlank() }?.let {
            appendLine()
            append("Заметка: ")
            append(it)
        }
    }
}