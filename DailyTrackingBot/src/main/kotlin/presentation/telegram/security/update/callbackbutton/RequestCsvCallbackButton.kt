package presentation.telegram.security.update.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object RequestCsvCallbackButton : CallbackButton("requestCsv") {
    fun getCallbackData(ticker: String) = InlineKeyboardButton.CallbackData(
        text = "Получить CSV",
        callbackData = callbackName + QUERY_SEPARATOR + ticker
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        if (arguments.size != 1) return null
        return CallbackData(arguments.first())
    }

    data class CallbackData(
        val ticker: String
    )
}