package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.common.getFutureSharePrice
import domain.common.percentBetweenDoubles
import domain.tinkoff.model.FullTinkoffSecurity
import domain.tinkoff.model.TinkoffPrice
import domain.user.common.DEFAULT_SHARE_PERCENT
import presentation.telegram.TelegramBot
import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.common.PERCENT_FMT
import java.util.*

class SecuritySearchResult(id: Long, messageId: Long?, val ticker: String, val state: State) :
    BotScreen(id, messageId) {
    sealed class State(val followed: Boolean) {
        class SearchResult(
            followed: Boolean,
            val result: FullTinkoffSecurity
        ) : State(followed)

        class FollowUpdate(
            followed: Boolean,
            val messageText: String
        ) : State(followed)
    }

    override val text = calculateText()
    override val replyMarkup = calculateReplyMarkup()
    override val parseMode = null

    private fun calculateText(): String {
        return when (state) {
            is State.FollowUpdate -> state.messageText
            is State.SearchResult -> resultToText(state.result)
        }
    }

    private fun resultToText(result: FullTinkoffSecurity): String {
        val res = StringBuilder()
        res.append(result.security.share.ticker)
        res.append(" - ")
        res.append(result.security.share.name)
        res.append(": ")
        res.append(result.sharePrice.price)
        res.append(TelegramBot.ROUBLE)


//        var string = "${result.security.share.ticker} - ${result.security.share.name}: ${result.sharePrice.price}${TelegramBot.ROUBLE}"
        result.security.futures.forEachIndexed { index, future ->
            val price = result.futuresPrices.getOrElse(index) { TinkoffPrice() }
//            string += "\nФьючерс ${future.ticker} - ${future.name}: ${price.price}${TelegramBot.ROUBLE}"
            res.append("\nФьючерс ")
            res.append(future.ticker)
            res.append(" - ")
            res.append(future.name)
            res.append(": ")
            res.append(price.price)
            res.append(TelegramBot.ROUBLE)

            val futurePrice = getFutureSharePrice(result.sharePrice.price, price.price)
            if (futurePrice > 0.0) {
                val percent = percentBetweenDoubles(result.sharePrice.price, futurePrice)
                res.append(" (")
                if (percent >= DEFAULT_SHARE_PERCENT) res.append('❗')
                res.append(PERCENT_FMT.format(Locale.US, percent))
                res.append("%)")
            }
        }

        return res.toString()
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
