package presentation.telegram.security.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ToggleIsActiveCallbackButton : CallbackButton("toggle_is_active") {
    fun getCallbackData(currentValue: Boolean) = InlineKeyboardButton.CallbackData(
        text = if (currentValue) "Отключить уведомления" else "Включить уведомления",
        callbackData = callbackName + QUERY_SEPARATOR + !currentValue
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.toBooleanStrictOrNull()?.let {
            return CallbackData(it)
        }
        return null
    }

    data class CallbackData(
        val newValue: Boolean
    )
}