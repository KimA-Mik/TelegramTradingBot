package presentation.telegram.security.search.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.common.ROUBLE_SIGN
import domain.common.formatToRu
import domain.tinkoff.usecase.FindSecurityUseCase
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.search.callbackbutton.SubscribeToSecurityCallbackButton
import presentation.telegram.security.search.callbackbutton.TickerSuggestionCallbackButton
import presentation.telegram.security.search.callbackbutton.UnsubscribeFromSecurityCallbackButton
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Share
import ru.kima.telegrambot.common.util.TelegramUtil

class TickerSearchResultScreen(
    userId: Long,
    messageId: Long? = null,
    private val searchResult: FindSecurityUseCase.Result
) : BotScreen(userId, messageId) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN
    override val replyMarkup = calculateReplayMarkup()
    override val disableWebPagePreview = true

    private fun renderText(): String = when (searchResult) {
        FindSecurityUseCase.Result.NotFound -> "Ничего не найдено"
        is FindSecurityUseCase.Result.Suggestions -> "Точного совпадения не найдено, возможно вы имели в виду:"
        is FindSecurityUseCase.Result.Success -> buildString {
            append(
                when (searchResult.security) {
                    is Share -> "Найдена акция "
                    is Future -> "Найден фьючерс "
                }
            )
            append(TelegramUtil.clickableSecurity(searchResult.security))
            searchResult.price?.let {
                append(" — ")
                append(it.formatToRu())
                append(ROUBLE_SIGN)
            }
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup? = when (searchResult) {
        is FindSecurityUseCase.Result.Suggestions -> InlineKeyboardMarkup.create(
            buttons = buildList {
                searchResult.suggestions.forEach { ticker ->
                    add(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = ticker,
                                callbackData = TickerSuggestionCallbackButton.getCallbackData(ticker)
                            )
                        )
                    )
                }
            }
        )

        is FindSecurityUseCase.Result.Success -> InlineKeyboardMarkup.create(
            listOf(
                if (searchResult.subscribed) UnsubscribeFromSecurityCallbackButton.getCallbackData(searchResult.security.ticker)
                else SubscribeToSecurityCallbackButton.getCallbackData(searchResult.security.ticker)
            )
        )

        else -> null
    }
}