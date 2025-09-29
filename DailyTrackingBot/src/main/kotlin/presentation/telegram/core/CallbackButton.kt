package presentation.telegram.core

abstract class CallbackButton(val callbackData: String) {
    companion object {
        const val QUERY_SEPARATOR = '&'
    }
}
