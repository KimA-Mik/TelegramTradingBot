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
        when (update.type) {
            is TelegramUpdate.PriceAlert.PriceType.Target -> appendLine("*Цена продажи:* ${update.security.targetPrice.formatToRu()}$ROUBLE_SIGN")
            is TelegramUpdate.PriceAlert.PriceType.LowTarget -> appendLine("*Цена покупки:* ${update.security.lowTargetPrice.formatToRu()}$ROUBLE_SIGN")
            is TelegramUpdate.PriceAlert.PriceType.All -> {
                appendLine("*Цена покупки:* ${update.security.lowTargetPrice.formatToRu()}$ROUBLE_SIGN")
                appendLine("*Цена продажи:* ${update.security.targetPrice.formatToRu()}$ROUBLE_SIGN")
            }
        }

        when (update.type) {
            is TelegramUpdate.PriceAlert.PriceType.All -> {
                appendLine("*Отклонение от цены продажи:* ${update.type.deviation.formatToRu()}%")
                appendLine("*Отклонение от цены покупки:* ${update.type.lowDeviation.formatToRu()}%")
            }

            else -> appendLine("*Отклонение:* ${update.type.deviation.formatToRu()}%")
        }

        update.indicators?.let { ind ->
            appendLine()
            appendLine("*Индикаторы:*")
            appendLine("*${PresentationUtil.rsiColor(ind.min15Rsi)}RSI (15м):* ${ind.min15Rsi.formatToRu()}")
            appendLine("*${PresentationUtil.rsiColor(ind.hourlyRsi)}RSI (1ч):* ${ind.hourlyRsi.formatToRu()}")
            appendLine("*${PresentationUtil.rsiColor(ind.dailyRsi)}RSI (1д):* ${ind.dailyRsi.formatToRu()}")
            var bbColor = PresentationUtil.markupBbColor(ind.min15bb.middle, ind.min15bb.lower, ind.min15bb.upper)
            append("*${bbColor}BB (15м):* верхняя: ${ind.min15bb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.min15bb.lower.formatToRu()}")
            bbColor = PresentationUtil.markupBbColor(ind.hourlyBb.middle, ind.hourlyBb.lower, ind.hourlyBb.upper)
            append("*${bbColor}BB (1ч):* верхняя: ${ind.hourlyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.hourlyBb.lower.formatToRu()}")
            bbColor = PresentationUtil.markupBbColor(ind.dailyBb.middle, ind.dailyBb.lower, ind.dailyBb.upper)
            append("*${bbColor}BB (1д):* верхняя: ${ind.dailyBb.upper.formatToRu()}, ")
            appendLine("нижняя: ${ind.dailyBb.lower.formatToRu()}")
        }
        update.security.note?.takeIf { it.isNotBlank() }?.let {
            appendLine()
            append("Заметка: ")
            append(it)
        }
    }
}