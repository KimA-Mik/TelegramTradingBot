package presentation.telegram.security.callbackbutton

import presentation.telegram.core.CallbackButton

object ToggleShowNoteCallbackButton : CallbackButton("toggle_show_note") {
    fun getCallbackData(currentValue: Boolean) = callbackName + QUERY_SEPARATOR + !currentValue
    fun getText(currentValue: Boolean): String = if (currentValue) "Скрыть заметку" else "Показать заметку"
    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.toBooleanStrictOrNull()?.let {
            return CallbackData(it)
        }
        return null
    }

    data class CallbackData(
        val showNote: Boolean
    )
}