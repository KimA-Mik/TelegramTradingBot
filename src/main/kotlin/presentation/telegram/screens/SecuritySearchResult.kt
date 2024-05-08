package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.tinkoff.model.DisplayShare
import domain.user.model.User
import presentation.common.MarkdownUtil
import presentation.common.TinInvestUtil
import presentation.common.formatAndTrim
import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.common.ROUBLE_SIGN

class SecuritySearchResult(val user: User, messageId: Long?, val ticker: String, val state: State) :
    BotScreen(user.id, messageId) {
    sealed class State(val followed: Boolean) {
        class SearchResult(
            followed: Boolean,
            val result: DisplayShare,
        ) : State(followed)

        class FollowUpdate(
            followed: Boolean,
            val messageText: String
        ) : State(followed)
    }

    override val text = calculateText()
    override val replyMarkup = calculateReplyMarkup()
    override val parseMode = ParseMode.MARKDOWN
    override val disableWebPagePreview = true

    private fun calculateText(): String {
        return when (state) {
            is State.FollowUpdate -> state.messageText
            is State.SearchResult -> resultToText(state.result)
        }
    }

    private fun resultToText(result: DisplayShare): String {
        val sb = StringBuilder()
//        result.priceDateTime?.let {
//            sb.append("[")
//            sb.append(it.format(priceDateFormat))
//            sb.append("] ")
//        }
        sb.append(
            MarkdownUtil.inlineUrl(
                text = result.ticker,
                url = TinInvestUtil.shareUrl(result.ticker)
            )
        )
        sb.append(" - ")
        sb.append(result.name)
        sb.append(": ")
        if (result.price > 0.0) {
            sb.append(result.price.formatAndTrim(2))
            sb.append(ROUBLE_SIGN)
            sb.append(' ')
        }

//        var string = "${result.security.share.ticker} - ${result.security.share.name}: ${result.sharePrice.price}${ROUBLE_SIGN}"
        result.futures.forEach { future ->
//            string += "\nФьючерс ${future.ticker} - ${future.name}: ${price.price}${TelegramBot.ROUBLE}"
//            future.priceDateTime?.let {
//                sb.append("\n[")
//                sb.append(it.format(priceDateFormat))
//                sb.append("] ")
//            }
            sb.append('\n')
            sb.append(
                MarkdownUtil.inlineUrl(
                    text = future.ticker,
                    url = TinInvestUtil.futureUrl(future.ticker)
                )
            )
            sb.append(" - ")
            sb.append(future.name)

            if (future.price == 0.0) return@forEach
            sb.append(": ")
            sb.append(future.price.formatAndTrim(2))
            sb.append(ROUBLE_SIGN)

            if (future.percent != 0.0) {
                sb.append(" (")
//                if (future.percent >= DEFAULT_SHARE_PERCENT) sb.append('❗')
                sb.append(future.percent.formatAndTrim(2))
                sb.append("%), ")
            }

            if (future.annualPercent != 0.0) {
                sb.append("Годовые: ")
                if (future.annualPercent >= user.defaultPercent) sb.append('❗')
                sb.append(future.annualPercent.formatAndTrim(2))
                sb.append("%, после налога: ")
                sb.append(future.annualAfterTaxes.formatAndTrim(2))
                sb.append('%')
            }
        }

        return sb.toString()
    }

    private fun calculateReplyMarkup(): ReplyMarkup {
        val button = when (state.followed) {
            false -> CallbackButton.Subscribe
            true -> CallbackButton.Unsubscribe
        }

        return InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    button.text,
                    button.callbackData
                            + CALLBACK_BUTTON_ARGUMENT_SEPARATOR
                            + ticker
                )
            )
        )
    }
}
