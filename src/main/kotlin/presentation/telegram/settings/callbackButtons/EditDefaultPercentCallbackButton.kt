package presentation.telegram.settings.callbackButtons

import presentation.common.formatAndTrim
import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton
import kotlin.math.abs

data object EditDefaultPercentCallbackButton : CallbackButton("", "editDefaultPercent") {
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

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        val percent = arguments.firstOrNull()?.toDoubleOrNull() ?: return null

        return CallbackData(percent)
    }

    data class CallbackData(val percent: Double)
}