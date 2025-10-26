package presentation.telegram.security.edit.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ResetPriceCallbackButton : CallbackButton("reset_price") {
    fun getCallbackData(ticker: String) = InlineKeyboardButton.CallbackData(
        "Сбросить цену",
        callbackName + QUERY_SEPARATOR + ticker
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val ticker = arguments.getOrNull(0) ?: return null
        return CallbackData(ticker)
    }

    data class CallbackData(
        val ticker: String
    )
}