package presentation.telegram.securitiesList.callbackButtons

import presentation.common.format
import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton
import kotlin.math.abs

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
