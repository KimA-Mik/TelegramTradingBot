package presentation.telegram.security.edit.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.user.model.SecurityType
import domain.user.model.TrackingSecurity
import domain.user.model.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.edit.callbackbutton.ResetPriceCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleIsActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleRemainActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleShowNoteCallbackButton
import presentation.util.TelegramUtil
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SecurityScreen(
    user: User,
    private val security: TrackingSecurity?,
    private val lastPrice: Double?,
    messageId: Long? = null
) : BotScreen(user.id, messageId) {
    override val text = renderText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    @OptIn(ExperimentalTime::class)
    private fun renderText(): String = buildString {
        if (security == null) {
            append("Бумага не найдена в базе.")
            return@buildString
        }

        when (security.type) {
            SecurityType.FUTURE -> append(
                "Фьючерс \"${security.name}\" (${
                    TelegramUtil.clickableTrackingSecurity(
                        security
                    )
                })"
            )

            SecurityType.SHARE -> append("Акция \"${security.name}\" (${TelegramUtil.clickableTrackingSecurity(security)})")
        }

        if (lastPrice != null) {
            appendLine(" сейчас ${lastPrice.formatToRu()}${ROUBLE_SIGN}")
        } else {
            appendLine()
        }

        append("Планируемая цена продажи: ")
        appendLine("*${security.targetPrice.formatToRu()}${ROUBLE_SIGN}*")
        append("Планируемая ценa покупки: ")
        appendLine("*${security.lowTargetPrice.formatToRu()}${ROUBLE_SIGN}*")

        append("Отклонение для срабатывания сигнала: ")
        appendLine("*${security.targetDeviation.formatToRu()}%*")

        append("Отслеживание: ")
        if (security.isActive) {
            appendLine("*включено*")
        } else {
            appendLine("*выключено*")
        }

        append("Поддержание отслеживания активным: ")
        if (security.remainActive) {
            appendLine("*включено*")
        } else {
            appendLine("*выключено*")
        }

        if (security.showNote) {
            append('\n')
            append("Заметка ")
            if (security.note == null) {
                append("не установлена")
            } else {
                security.noteUpdatedMs?.let {
                    val ldt = Instant.fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    append(" (", ldt.format(TelegramUtil.localDateTimeFormat), ")")
                }
                appendLine(":")
                append(security.note)
            }
        }
    }

    private fun calculateReplayMarkup() = if (security == null) null
    else InlineKeyboardMarkup.create(
        listOf(
            listOf(
                ToggleIsActiveCallbackButton.getCallbackData(security.ticker, security.isActive),
            ),
            listOf(
                ToggleRemainActiveCallbackButton.getCallbackData(security.ticker, security.remainActive)
            ),
            listOf(
                ToggleShowNoteCallbackButton.getCallbackData(security.ticker, security.showNote)
            ),
            listOf(
                ResetPriceCallbackButton.getCallbackData(security.ticker)
            )
        )
    )
}