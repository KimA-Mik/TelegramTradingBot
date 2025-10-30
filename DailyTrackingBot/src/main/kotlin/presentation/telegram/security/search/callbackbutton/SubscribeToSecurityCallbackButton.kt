package presentation.telegram.security.search.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object SubscribeToSecurityCallbackButton : CallbackButton("subscribe_to_security") {
    private const val TEXT = "Подписаться"
    fun getCallbackData(ticker: String) = InlineKeyboardButton.CallbackData(
        text = TEXT,
        callbackName + QUERY_SEPARATOR + ticker
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.let {
            return CallbackData(it)
        }
        return null
    }

    data class CallbackData(val ticker: String)
}