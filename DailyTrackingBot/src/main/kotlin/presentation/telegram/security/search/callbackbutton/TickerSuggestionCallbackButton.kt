package presentation.telegram.security.search.callbackbutton

import presentation.telegram.core.CallbackButton

data object TickerSuggestionCallbackButton : CallbackButton(callbackName = "ticker_suggestion") {
    fun getCallbackData(ticker: String) = callbackName + QUERY_SEPARATOR + ticker
    fun parseCallbackData(arguments: List<String>): CallbackData? {
        arguments.firstOrNull()?.let {
            return CallbackData(it)
        }
        return null
    }

    data class CallbackData(val ticker: String)
}