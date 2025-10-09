package presentation.telegram.security.edit.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ToggleRemainActiveCallbackButton : CallbackButton("toggle_remain_active") {
    fun getCallbackData(ticker: String, currentValue: Boolean) = InlineKeyboardButton.CallbackData(
        text = if (currentValue) "Отключить продление" else "Включить продление",
        callbackData = callbackName + QUERY_SEPARATOR + ticker + QUERY_SEPARATOR + !currentValue
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val ticker = arguments.getOrNull(0) ?: return null
        val newValue = arguments.getOrNull(1)?.toBooleanStrictOrNull() ?: return null
        return CallbackData(ticker, newValue)
    }

    data class CallbackData(
        val ticker: String,
        val newValue: Boolean
    )
}