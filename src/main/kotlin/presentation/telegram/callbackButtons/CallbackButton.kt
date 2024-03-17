package presentation.telegram.callbackButtons

import presentation.telegram.common.format
import kotlin.math.abs

sealed class CallbackButton(val text: String, val callbackData: String) {
    data object Subscribe : CallbackButton("Отслеживать", "followSecurity")
    data object Unsubscribe : CallbackButton("Перестать отслеживать", "unfollowSecurity")
    data object SecuritiesListBack : CallbackButton("Предыдущая страница", "securitiesListBack")
    data object SecuritiesListForward : CallbackButton("Следующая страница", "securitiesListForward")
    data object EditShare : CallbackButton("Изменить ", "editSecurity")
    data object SharePercent : CallbackButton("Процент: ", "sharePercent") {
        fun getText(percent: Double): String {
            val sign = if (percent < 0.0) '-' else '+'
            return text +
                    sign +
                    abs(percent) +
                    '%'
        }

        fun getCallbackData(ticker: String, percent: Double): String {
            return callbackData +
                    CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                    ticker +
                    CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                    percent.format(2)
        }
    }
}
