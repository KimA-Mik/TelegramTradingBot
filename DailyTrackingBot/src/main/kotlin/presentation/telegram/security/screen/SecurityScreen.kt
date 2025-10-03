package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatAndTrim
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.callbackbutton.ToggleIsActiveCallbackButton
import presentation.telegram.security.callbackbutton.ToggleRemainActiveCallbackButton
import presentation.telegram.security.callbackbutton.ToggleShowNoteCallbackButton
import presentation.util.TelegramUtil
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.Share

class SecurityScreen(
    private val user: User,
    private val security: Security?,
    private val lastPrice: Double?,
    messageId: Long? = null
) : BotScreen(user.id, messageId) {
    override val text = renderText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun renderText(): String = buildString {
        append("Бумага: ")
        when (security) {
            is Future -> appendLine("Фьючерс \"${security.name}\" (${TelegramUtil.clickableSecurity(security)})")
            is Share -> appendLine("Акция \"${security.name}\" (${TelegramUtil.clickableSecurity(security)})")
            null -> appendLine("не выбрана")
        }

        append("Текущая отслеживаемая ценa: ")
        if (lastPrice != null) {
            append("*${lastPrice.formatAndTrim(2)}$ROUBLE_SIGN*")
        } else {
            append("не установлена")
        }

        if (user.targetDeviation == null) {
            append(", отклонение не установлено")
        } else {
            append(" с отклонением ")
            append("*${user.targetDeviation.formatAndTrim(2)}%*")
        }

        append('\n')
        append("Отслеживание: ")
        if (user.isActive) {
            appendLine("*включено*")
        } else {
            appendLine("*выключено*")
        }

        append("Поддержание отслеживания активным: ")
        if (user.remainActive) {
            appendLine("*включено*")
        } else {
            appendLine("*выключено*")
        }

        if (user.showNote) {
            append('\n')
            append("Заметка: ")
            if (user.note == null) {
                append("не установлена")
            } else {
                append(user.note)
            }
        }
    }

    private fun calculateReplayMarkup() = InlineKeyboardMarkup.create(
        listOf(
            listOf(
                ToggleIsActiveCallbackButton.getCallbackData(user.isActive),
            ),
            listOf(
                ToggleRemainActiveCallbackButton.getCallbackData(user.remainActive)
            ),
            listOf(
                ToggleShowNoteCallbackButton.getCallbackData(user.showNote)
            )
        )
    )
}