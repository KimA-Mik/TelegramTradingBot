package presentation.telegram.security.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.updateservice.TelegramUpdate
import domain.util.MathUtil
import presentation.util.PresentationUtil

class UnboundPriceAlertScreen(
    private val update: TelegramUpdate.UnboundPriceAlert
) : SecurityAlertScreen(update.user.id) {
    override val text
        get() = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true
    override val replyMarkup = defaultSecurityAlertReplayMarkup(update.security)

    private fun renderText() = buildString(UPDATE_BUILDER_CAPACITY) {
        append("*Сработал ценовой сигнал!* ")
        appendLine(alertColor(update))
        renderSecurityTitleForAlert(update.security)

        append("*Текущая цена:* ${update.currentPrice.formatToRu()}${ROUBLE_SIGN} ")
        when (update.type) {
            TelegramUpdate.UnboundPriceAlert.PriceType.ABOVE -> {
                update.security.targetPrice?.let {
                    val diff = MathUtil.absolutePercentageDifference(update.currentPrice, it)
                    append("на ", diff.formatToRu(), "% выше планируемой цены продажи (")
                    append(update.security.targetPrice.formatToRu(), ROUBLE_SIGN, ')')
                }
            }

            TelegramUpdate.UnboundPriceAlert.PriceType.BELOW -> {
                update.security.lowTargetPrice?.let {
                    val diff = MathUtil.absolutePercentageDifference(update.currentPrice, it)
                    append("на ", diff.formatToRu(), "% ниже планируемой цены покупки (")
                    append(update.security.lowTargetPrice.formatToRu(), ROUBLE_SIGN, ')')
                }
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(update.indicators, update.currentPrice)
        appendNoteToSecurityAlert(update.security, hideProblematicUserNote)
    }

    private fun alertColor(update: TelegramUpdate.UnboundPriceAlert): String {
        return when (update.type) {
            TelegramUpdate.UnboundPriceAlert.PriceType.ABOVE -> PresentationUtil.RED
            TelegramUpdate.UnboundPriceAlert.PriceType.BELOW -> PresentationUtil.GREEN
        }
    }
}