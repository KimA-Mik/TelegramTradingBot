package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.tinkoff.model.FullTinkoffSecurity
import domain.tinkoff.model.TinkoffPrice
import presentation.telegram.App
import presentation.telegram.callbackButtons.CallbackButton

class SecuritySearchResult(id: Long, messageId: Long?, val state: State) : BotScreen(id, messageId) {
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
        var string =
            "${result.security.share.ticker} - ${result.security.share.name}: ${result.sharePrice.price}${App.ROUBLE}"
        result.security.futures.forEachIndexed { index, future ->
            val price = result.futuresPrices.getOrElse(index) { TinkoffPrice() }
            string += "\nФьючерс ${future.ticker} - ${future.name}: ${price.price}${App.ROUBLE}"
        }

        return string
    }

    private fun calculateReplyMarkup(): ReplyMarkup {
        val button = when (state.followed) {
            false -> CallbackButton.Subscribe
            true -> CallbackButton.Unsubscribe
        }

        return InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(button.text, button.callbackData)
            )
        )
    }
}
