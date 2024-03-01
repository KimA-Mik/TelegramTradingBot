package presentation.telegram.screens

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.tinkoff.model.FullTinkoffSecurity
import domain.tinkoff.model.TinkoffPrice
import presentation.telegram.App

class SecuritySearchResult(id: Long, messageId: Long?, val result: FullTinkoffSecurity) : BotScreen(id, messageId) {
    sealed interface State {
        data class SearchResult(val result: FullTinkoffSecurity) : State
        data class FollowUpdate(
            val messageText: String,
            val followed: Boolean
        ) : State
    }

    override val text = calculateText()
    override val replyMarkup = calculateReplyMarkup()

    private fun calculateText(): String {
        var string =
            "${result.security.share.ticker} - ${result.security.share.name}: ${result.sharePrice.price}${App.ROUBLE}"
        result.security.futures.forEachIndexed { index, future ->
            val price = result.futuresPrices.getOrElse(index) { TinkoffPrice() }
            string += "\nФьючерс ${future.ticker} - ${future.name}: ${price.price}${App.ROUBLE}"
        }

        return string
    }

    override val parseMode = null
    private fun calculateReplyMarkup(): ReplyMarkup {
        return InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData("Отслеживать", "followSecurity")
            )
        )
    }
}
