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
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

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
                appendLine("*Планируемая цена покупки:* ${update.security.lowTargetPrice.formatToRu()}$ROUBLE_SIGN")
                appendLine("*Планируемая цена продажи:* ${update.security.targetPrice.formatToRu()}$ROUBLE_SIGN")
            }
        }

        when (update.type) {
            is TelegramUpdate.PriceAlert.PriceType.All -> {
                appendLine("*Отклонение от цены продажи:* ${update.type.deviation.formatToRu()}%")
                appendLine("*Отклонение от цены покупки:* ${update.type.lowDeviation.formatToRu()}%")
            }

            else -> appendLine("*Отклонение:* ${update.type.deviation.formatToRu()}%")
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice)
        appendNoteToSecurityAlert(update.security)
    }
}