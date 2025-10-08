package presentation.telegram.security.edit.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ToggleRemainActiveCallbackButton : CallbackButton("toggle_remain_active") {
    fun getCallbackData(currentValue: Boolean) = InlineKeyboardButton.CallbackData(
        text = if (currentValue) "Отключить продление" else "Включить продление",
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