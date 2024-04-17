package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.common.TelegramUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.common.NOT_SUBSCRIBED_TO_SHARE

class EditShareScreen(
    id: Long,
    messageId: Long? = null,
    val state: State
) : BotScreen(id, messageId) {
    override val text = markupText()
    override val replyMarkup = calculateReplayMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun markupText(): String {
        return when (state) {
            is State.NotSubscribed -> NOT_SUBSCRIBED_TO_SHARE + state.ticker
            is State.Share -> {
                val shareInlineUrl = TelegramUtil.markdownInlineUrl(
                    text = state.ticker,
                    url = TinInvestUtil.shareUrl(state.ticker)
                )
                "$shareInlineUrl - ${state.percent.formatAndTrim(2)}%"
            }
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        return when (state) {
            is State.Share -> getActualMarkup(state)
            is State.NotSubscribed -> null
        }
    }

    private fun getActualMarkup(state: State.Share): ReplyMarkup {
        InlineKeyboardButton.CallbackData("", "")
        val rows = listOf(
            listOf(
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SharePercent.getText(-1.0),
                    CallbackButton.SharePercent.getCallbackData(state.ticker, -1.0)
                ),
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SharePercent.getText(1.0),
                    CallbackButton.SharePercent.getCallbackData(state.ticker, 1.0)
                ),
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SharePercent.getText(-0.1),
                    CallbackButton.SharePercent.getCallbackData(state.ticker, -0.1)
                ),
                InlineKeyboardButton.CallbackData(
                    CallbackButton.SharePercent.getText(0.1),
                    CallbackButton.SharePercent.getCallbackData(state.ticker, 0.1)
                ),
            )
        )

        return InlineKeyboardMarkup.create(rows)
    }

    sealed interface State {
        data class Share(val ticker: String, val percent: Double) : State
        data class NotSubscribed(val ticker: String) : State
    }
}