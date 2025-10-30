package presentation.telegram.security.search.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object UnsubscribeFromSecurityCallbackButton : CallbackButton("unsubscribe_from_security") {
    private const val TEXT = "Отписаться"
    fun getCallbackData(ticker: String, popBack: Boolean = false) = InlineKeyboardButton.CallbackData(
        text = TEXT,
        callbackData = callbackName + QUERY_SEPARATOR + ticker + QUERY_SEPARATOR + popBack
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        if (arguments.size != 2) return null
        val ticker = arguments[0]
        val popBack = arguments[1].toBooleanStrictOrNull() ?: return null
        return CallbackData(ticker, popBack)
    }

    data class CallbackData(val ticker: String, val popBack: Boolean)
}