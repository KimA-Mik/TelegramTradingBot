package presentation.telegram.settings.callbackButtons

import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton

object LinkAgentAccountCallbackButton : CallbackButton(
    "Привязать аккаунт Agent", "linkAgentAccount"
) {
    fun getCallbackData(userId: Long) =
        callbackData +
                CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                userId

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val userId = arguments.firstOrNull()?.toLongOrNull() ?: return null

        return CallbackData(userId)
    }

    data class CallbackData(val userId: Long)
}