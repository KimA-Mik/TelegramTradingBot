package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.updateService.model.NotifyShare
import presentation.common.mappers.toUpdateText
import presentation.telegram.callbackButtons.CallbackButton

class FuturePriceUpdate(
    userId: Long,
    messageId: Long? = null,
    private val state: State
) : BotScreen(userId, messageId) {
    override val text = markupText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun markupText(): String {
        return when (state) {
            is State.ResetNotify -> state.originalText + "\n\\[Уведомление сброшено]"
            is State.UnableResetNotify -> state.originalText + "\n\\[Сброс не требуется]"
            is State.ShowUpdate -> state.share.toUpdateText()
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup {
        return when (state) {
            is State.ResetNotify -> InlineKeyboardMarkup.create(emptyList<InlineKeyboardButton.CallbackData>())
            is State.UnableResetNotify -> InlineKeyboardMarkup.create(emptyList<InlineKeyboardButton.CallbackData>())
            is State.ShowUpdate -> InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.ResetNotification.text,
                        CallbackButton.ResetNotification.getCallbackData(state.share.shareTicker)
                    )
                )
            )

        }
    }

    sealed interface State {
        data class ShowUpdate(
            val share: NotifyShare
        ) : State

        data class ResetNotify(
            val originalText: String
        ) : State

        data class UnableResetNotify(
            val originalText: String
        ) : State

    }
}