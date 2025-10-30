package presentation.telegram.security.list.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object EditSecurityCallbackButton : CallbackButton("edit_security") {
    fun getCallbackData(ticker: String) = InlineKeyboardButton.CallbackData(
        text = ticker,
        callbackData = callbackName + QUERY_SEPARATOR + ticker
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.let {
            return CallbackData(it)
        }
        return null
    }

    data class CallbackData(val ticker: String)
}