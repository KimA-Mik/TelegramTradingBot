package presentation.telegram.securitiesList.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.user.model.UserShare
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.telegram.common.NOT_SUBSCRIBED_TO_SHARE
import presentation.telegram.screens.BotScreen
import presentation.telegram.securitiesList.callbackButtons.SharePercent
import presentation.telegram.securitiesList.callbackButtons.SwitchShareIndicatorCallbackButton
import presentation.telegram.settings.IndicatorType
import presentation.telegram.settings.stateToText

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
                val shareInlineUrl = MarkdownUtil.inlineUrl(
                    text = state.share.ticker,
                    url = TinInvestUtil.shareUrl(state.share.ticker)
                )
                "$shareInlineUrl - ${state.share.percent.formatAndTrim(2)}%\n" +
                        "RSI: ${stateToText(state.share.rsiNotificationsEnabled)}\n" +
                        "Полосы Боллинджера: ${stateToText(state.share.bbNotificationsEnabled)}\n"
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
                    SharePercent.getText(-1.0),
                    SharePercent.getCallbackData(state.share.ticker, -1.0)
                ),
                InlineKeyboardButton.CallbackData(
                    SharePercent.getText(1.0),
                    SharePercent.getCallbackData(state.share.ticker, 1.0)
                ),
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    SharePercent.getText(-0.1),
                    SharePercent.getCallbackData(state.share.ticker, -0.1)
                ),
                InlineKeyboardButton.CallbackData(
                    SharePercent.getText(0.1),
                    SharePercent.getCallbackData(state.share.ticker, 0.1)
                ),
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    SwitchShareIndicatorCallbackButton.getText(
                        enabled = state.share.rsiNotificationsEnabled,
                        indicatorType = IndicatorType.RSI
                    ),
                    SwitchShareIndicatorCallbackButton.getCallbackData(
                        ticker = state.share.ticker, enabled = state.share.rsiNotificationsEnabled,
                        indicatorType = IndicatorType.RSI
                    )
                ),
                InlineKeyboardButton.CallbackData(
                    SwitchShareIndicatorCallbackButton.getText(
                        enabled = state.share.bbNotificationsEnabled,
                        indicatorType = IndicatorType.BOLLINGER_BANDS
                    ),
                    SwitchShareIndicatorCallbackButton.getCallbackData(
                        ticker = state.share.ticker, enabled = state.share.bbNotificationsEnabled,
                        indicatorType = IndicatorType.BOLLINGER_BANDS
                    )
                ),
            )
        )

        return InlineKeyboardMarkup.create(rows)
    }

    sealed interface State {
        data class Share(val share: UserShare) : State
        data class NotSubscribed(val ticker: String) : State
    }
}