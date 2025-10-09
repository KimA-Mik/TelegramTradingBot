package presentation.telegram.security.list.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.user.model.SecurityType.FUTURE
import domain.user.model.SecurityType.SHARE
import domain.user.model.TrackingSecurity
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.edit.callbackbutton.ToggleIsActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleRemainActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleShowNoteCallbackButton
import presentation.util.TelegramUtil

class SecurityScreen(
    private val user: User,
    private val security: TrackingSecurity?,
    private val lastPrice: Double?,
    messageId: Long? = null
) : BotScreen(user.id, messageId) {
    override val text = renderText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun renderText(): String = buildString {
        if (security == null) {
            append("Бумага не найдена в базе.")
            return@buildString
        }

        append("Бумага: ")
        when (security.type) {
            FUTURE -> append("Фьючерс \"${security.name}\" (${TelegramUtil.clickableTrackingSecurity(security)})")
            SHARE -> append("Акция \"${security.name}\" (${TelegramUtil.clickableTrackingSecurity(security)})")
        }

        if (lastPrice != null) {
            appendLine(" по ${lastPrice.formatToRu()}$ROUBLE_SIGN")
        } else {
            appendLine()
        }

        append("Текущая отслеживаемая ценa: ")
        append("*${security.targetPrice.formatToRu()}$ROUBLE_SIGN*")

        append(" с отклонением ")
        append("*${security.targetDeviation.formatToRu()}%*")

        append('\n')
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
            append("Заметка: ")
            if (security.note == null) {
                append("не установлена")
            } else {
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
            )
        )
    )
}