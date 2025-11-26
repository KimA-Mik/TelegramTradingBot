package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import presentation.telegram.core.screen.BotScreen
import presentation.util.PresentationUtil

class PriceAlertScreen(
    private val update: TelegramUpdate.PriceAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        appendLine("*Сработал ценовой сигнал!*")
        renderSecurityTitleForAlert(update.security)

        appendLine("*Текущая цена:* ${update.currentPrice.formatToRu()}${ROUBLE_SIGN}")
        when (update.type) {
            is TelegramUpdate.PriceAlert.PriceType.Target ->
                appendLine("*Планируемая цена продажи:* ${PresentationUtil.formatTargetPrice(update.security.targetPrice)}")

            is TelegramUpdate.PriceAlert.PriceType.LowTarget ->
                appendLine("*Планируемая цена покупки:* ${PresentationUtil.formatTargetPrice(update.security.lowTargetPrice)}")

            is TelegramUpdate.PriceAlert.PriceType.All -> {
                appendLine("*Планируемая цена покупки:* ${PresentationUtil.formatTargetPrice(update.security.lowTargetPrice)}")
                appendLine("*Планируемая цена продажи:* ${PresentationUtil.formatTargetPrice(update.security.targetPrice)}")
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