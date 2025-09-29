package presentation.telegram.security.callbackbutton

import presentation.telegram.core.CallbackButton

data object TickerSuggestionCallbackButton : CallbackButton(callbackData = "tickerSuggestion") {
    fun getCallbackData(ticker: String) = callbackData + QUERY_SEPARATOR + ticker
    fun parseCallbackQuery(arguments: List<String>): CallbackQuery? {
        arguments.firstOrNull()?.let {
            return CallbackQuery(it)
        }
        return null
    }

    data class CallbackQuery(val ticker: String)
}