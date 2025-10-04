package presentation.telegram.security.screen.update

import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatAndTrim
import domain.updateservice.TelegramUpdate
import presentation.telegram.core.screen.BotScreen
import presentation.util.TelegramUtil
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Share

class RsiAlertScreen(
    private val update: TelegramUpdate.RsiAlert
) : BotScreen(update.user.id) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun renderText() = buildString {
        appendLine("*Сработал сигнал по RSI(15м)!*")
        appendLine()
        when (update.security) {
            is Future -> appendLine("*Фьючерс:* ${TelegramUtil.clickableSecurity(update.security)} — (${update.security.name})")
            is Share -> appendLine("*Акция:* ${TelegramUtil.clickableSecurity(update.security)} — (${update.security.name})")
        }
        appendLine("*Текущая цена:* ${update.currentPrice.formatAndTrim(2)}$ROUBLE_SIGN")
        appendLine("*Текущий RSI:* ${update.currentRsi.formatAndTrim(2)}")
        update.indicators?.let { ind ->
            appendLine()
            appendLine("*Индикаторы:*")
            appendLine("*RSI (1ч):* ${ind.hourlyRsi.formatAndTrim(2)}")
            appendLine("*RSI (1д):* ${ind.dailyRsi.formatAndTrim(2)}")
            append("*BB (15м):* верхняя: ${ind.min15bb.upper.formatAndTrim(2)}, ")
            appendLine("нижняя: ${ind.min15bb.lower.formatAndTrim(2)}")
            append("*BB (1ч):* верхняя: ${ind.hourlyBb.upper.formatAndTrim(2)}, ")
            appendLine("нижняя: ${ind.hourlyBb.lower.formatAndTrim(2)}")
            append("*BB (1д):* верхняя: ${ind.dailyBb.upper.formatAndTrim(2)}, ")
            appendLine("нижняя: ${ind.dailyBb.lower.formatAndTrim(2)}")
        }
        update.user.note?.takeIf { it.isNotBlank() }?.let {
            appendLine()
            append("Заметка: ")
            append(it)
        }
    }
}