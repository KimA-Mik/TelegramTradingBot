package presentation.telegram.securitiesList.callbackButtons

import presentation.telegram.callbackButtons.CALLBACK_BUTTON_ARGUMENT_SEPARATOR
import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.settings.DISABLE_SETTING
import presentation.telegram.settings.ENABLE_SETTING
import presentation.telegram.settings.IndicatorType

object SwitchShareIndicatorCallbackButton : CallbackButton(
    "Отключить уведомления через Agent", "switchShareIndicator"
) {
    data class CallbackData(
        val ticker: String,
        val newState: Boolean,
        val indicatorType: IndicatorType,
    )

    fun getText(
        enabled: Boolean,
        indicatorType: IndicatorType
    ): String {
        return when (enabled) {
            true -> DISABLE_SETTING
            false -> ENABLE_SETTING
        } + " ${indicatorType.shortName}"
    }

    fun getCallbackData(ticker: String, enabled: Boolean, indicatorType: IndicatorType): String {
        return callbackData +
                CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                ticker +
                CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                !enabled +
                CALLBACK_BUTTON_ARGUMENT_SEPARATOR +
                indicatorType
    }

    fun parseCallbackData(arguments: List<String>): CallbackData? {
        if (arguments.size != 3) {
            return null
        }

        val ticker = arguments[0]
        val enabled = arguments[1].toBooleanStrictOrNull() ?: return null
        val indicatorType = IndicatorType.fromName(arguments[2]) ?: return null

        return CallbackData(
            ticker = ticker,
            newState = enabled,
            indicatorType = indicatorType
        )
    }
}