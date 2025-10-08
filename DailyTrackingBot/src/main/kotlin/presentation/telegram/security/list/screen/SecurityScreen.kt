package presentation.telegram.security.list.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.user.model.User
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.edit.callbackbutton.ToggleIsActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleRemainActiveCallbackButton
import presentation.telegram.security.edit.callbackbutton.ToggleShowNoteCallbackButton
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
            is Future -> append("Фьючерс \"${security.name}\" (${TelegramUtil.clickableSecurity(security)})")
            is Share -> append("Акция \"${security.name}\" (${TelegramUtil.clickableSecurity(security)})")
            null -> appendLine("не выбрана")
        }

        if (lastPrice != null) {
            appendLine(" по ${lastPrice.formatToRu()}$ROUBLE_SIGN")
        } else {
            appendLine()
        }

        append("Текущая отслеживаемая ценa: ")
        if (user.targetPrice != null) {
            append("*${user.targetPrice.formatToRu()}$ROUBLE_SIGN*")
        } else {
            append("не установлена")
        }

        if (user.targetDeviation == null) {
            append(", отклонение не установлено")
        } else {
            append(" с отклонением ")
            append("*${user.targetDeviation.formatToRu()}%*")
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