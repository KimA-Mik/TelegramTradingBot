package presentation.telegram.security.list.callbackbutton

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import presentation.telegram.core.CallbackButton

object SecuritiesListForwardCallbackButton : CallbackButton("securities_list_forward") {
    const val TEXT = "➡"
    fun getCallbackData(currentPage: Int) = InlineKeyboardButton.CallbackData(
        text = TEXT,
        callbackData = callbackName + QUERY_SEPARATOR + currentPage
    )

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val page = arguments.firstOrNull()?.toIntOrNull() ?: return null
        return CallbackData(page)
    }

    data class CallbackData(val currentPage: Int)
}