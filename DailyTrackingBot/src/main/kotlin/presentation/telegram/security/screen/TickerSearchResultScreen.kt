package presentation.telegram.security.screen

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import domain.common.ROUBLE_SIGN
import domain.common.formatAndTrim
import domain.tinkoff.usecase.FindSecurityUseCase
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.callbackbutton.TickerSuggestionCallbackButton
import presentation.util.TelegramUtil
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Share

class TickerSearchResultScreen(
    userId: Long,
    messageId: Long? = null,
    private val searchResult: FindSecurityUseCase.Result
) : BotScreen(userId, messageId) {
    override val text = renderText()
    override val parseMode = ParseMode.MARKDOWN_V2
    override val replyMarkup = calculateReplayMarkup()

    private fun renderText(): String {
        return when (searchResult) {
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
                    append(it.formatAndTrim(2))
                    append(ROUBLE_SIGN)
                }
            }
        }
    }

    private fun calculateReplayMarkup(): ReplyMarkup? {
        return when (searchResult) {
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

            else -> null
        }
    }
}