package presentation.telegram.security.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object ToggleShowNoteCallbackButton : CallbackButton("toggle_show_note") {
    fun getCallbackData(currentValue: Boolean) = InlineKeyboardButton.CallbackData(
        text = if (currentValue) "Скрыть заметку" else "Показать заметку",
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