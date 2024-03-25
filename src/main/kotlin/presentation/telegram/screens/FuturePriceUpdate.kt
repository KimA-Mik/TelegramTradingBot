package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.updateService.model.NotifyShare
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.common.ROUBLE_SIGN
import presentation.telegram.common.formatAndTrim

class FuturePriceUpdate(
    userId: Long,
    messageId: Long? = null,
    private val state: State
) : BotScreen(userId, messageId) {
    override val text = markupText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = null

    private fun markupText(): String {
        return when (state) {
            is State.ResetNotify -> state.originalText + "\n[Уведомление сброшено]"
            is State.ShowUpdate -> state.share.toText()
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup {
        return when (state) {
            is State.ResetNotify -> InlineKeyboardMarkup.create(emptyList<InlineKeyboardButton.CallbackData>())
            is State.ShowUpdate -> InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        CallbackButton.ResetNotification.text,
                        CallbackButton.ResetNotification.getCallbackData(id, state.share.shareTicker)
                    )
                )
            )
        }
    }

    private fun NotifyShare.toText(): String {
        var res = String()
        res += "$shareTicker: ${sharePrice.formatAndTrim(2)}$ROUBLE_SIGN"

        futures.forEach { future ->
            res += "\n${future.ticker}: ${future.price.formatAndTrim(2)}$ROUBLE_SIGN (${future.actualDifference}%)"
        }

        return res
    }

    sealed interface State {
        data class ShowUpdate(
            val share: NotifyShare
        ) : State

        data class ResetNotify(
            val originalText: String
        ) : State
    }
}