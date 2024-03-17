package presentation.telegram.callbackButtons

sealed class CallbackButton(val text: String, val callbackData: String) {
    data object Subscribe : CallbackButton("Отслеживать", "followSecurity")
    data object Unsubscribe : CallbackButton("Перестать отслеживать", "unfollowSecurity")
    data object SecuritiesListBack : CallbackButton("Предыдущая страница", "securitiesListBack")
    data object SecuritiesListForward : CallbackButton("Следующая страница", "securitiesListForward")
    data object EditShare : CallbackButton("Изменить ", "editSecurity")
}
