package presentation.telegram.callbackButtons


abstract class CallbackButton(val text: String, val callbackData: String) {
    data object Subscribe : CallbackButton("Отслеживать", "followSecurity")
    data object Unsubscribe : CallbackButton("Перестать отслеживать", "unfollowSecurity")

    data object ResetNotification : CallbackButton("Сбросить уведомление", "resetNotification") {
        fun getCallbackData(shareTicker: String): String {
            return callbackData +
                    CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                    shareTicker
        }
    }
}
