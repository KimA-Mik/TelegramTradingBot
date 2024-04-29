package presentation.telegram.callbackButtons

import presentation.common.format
import presentation.common.formatAndTrim
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

    data object ResetNotification : CallbackButton("Сбросить уведомление", "resetNotification") {
        fun getCallbackData(shareTicker: String): String {
            return callbackData +
                    CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                    shareTicker
        }
    }

    //TODO: Get rid of sealed class
    data object EditDefaultPercent : CallbackButton("", "editDefaultPercent") {
        fun getText(percent: Double): String {
            val sign = if (percent > 0) '+' else '-'
            val f = abs(percent).formatAndTrim(2)
            return "${sign}${f}%"
        }

        fun getCallbackData(percent: Double): String {
            return callbackData +
                    CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                    percent.formatAndTrim(2)
        }
    }
}
