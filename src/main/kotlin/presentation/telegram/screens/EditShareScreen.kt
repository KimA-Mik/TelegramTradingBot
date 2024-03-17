package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.ReplyMarkup
import presentation.telegram.common.NOT_SUBSCRIBED_TO_SHARE
import presentation.telegram.common.PERCENT_FMT

class EditShareScreen(
    id: Long,
    messageId: Long? = null,
    val state: State
) : BotScreen(id, messageId) {
    override val text = markupText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = null

    private fun markupText(): String {
        return when (state) {
            is State.NotSubscribed -> NOT_SUBSCRIBED_TO_SHARE + state.ticker
            is State.Share -> "${state.ticker} - ${PERCENT_FMT.format(state.percent)}%"
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        return when (state) {
            is State.NotSubscribed -> null
            is State.Share -> null
        }
    }

    sealed interface State {
        data class Share(val ticker: String, val percent: Double) : State
        data class NotSubscribed(val ticker: String) : State
    }
}