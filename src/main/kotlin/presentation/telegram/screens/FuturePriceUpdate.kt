package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.updateService.model.NotifyShare
import domain.utils.DateUtil
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.common.futureDateFormat
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.common.ROUBLE_SIGN

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
            is State.ShowUpdate -> state.share.toText()
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

    private fun NotifyShare.toText(): String {
        var res = String()
        val inlineShareUrl = MarkdownUtil.inlineUrl(
            text = shareTicker,
            url = TinInvestUtil.shareUrl(shareTicker)
        )
        res += "$inlineShareUrl: ${sharePrice.formatAndTrim(2)}$ROUBLE_SIGN"

        futures.forEach { future ->
            val date = future.expirationDate.toLocalDateTime(DateUtil.timezoneMoscow).format(futureDateFormat)
            val inlineFutureUrl = MarkdownUtil.inlineUrl(
                text = future.ticker,
                url = TinInvestUtil.futureUrl(future.ticker)
            )
            res += "\n$date - $inlineFutureUrl: ${future.price.formatAndTrim(2)}$ROUBLE_SIGN, " +
                    "${future.annualPercent.formatAndTrim(2)}% годовых, " +
                    "после налога ${future.annualAfterTaxes.formatAndTrim(2)}%"
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

        data class UnableResetNotify(
            val originalText: String
        ) : State

    }
}