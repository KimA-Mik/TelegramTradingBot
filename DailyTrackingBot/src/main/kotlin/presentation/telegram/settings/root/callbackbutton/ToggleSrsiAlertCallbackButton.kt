package presentation.telegram.settings.root.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ToggleSrsiAlertCallbackButton : CallbackButton("toggle_srsi_alert") {
    fun getCallbackData(newValue: Boolean) = InlineKeyboardButton.CallbackData(
        text = if (newValue) "SRSI: ВКЛ" else "SRSI: ВЫКЛ",
        callbackData = callbackName + QUERY_SEPARATOR + newValue
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.toBooleanStrictOrNull()?.let { newValue ->
            return CallbackData(newValue)
        }
        return null
    }

    data class CallbackData(val newValue: Boolean)
}