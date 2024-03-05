package presentation.telegram.callbackButtons

sealed class CallbackButton(val text: String, val callbackData: String) {
    data object Subscribe : CallbackButton("Отслеживать", "followSecurity")
    data object Unsubscribe : CallbackButton("Перестать отслеживать", "unfollowSecurity")
}
