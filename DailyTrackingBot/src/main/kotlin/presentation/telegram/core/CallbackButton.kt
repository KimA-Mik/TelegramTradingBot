package presentation.telegram.core

abstract class CallbackButton(val callbackName: String) {
    companion object {
        const val QUERY_SEPARATOR = '&'
    }
}
