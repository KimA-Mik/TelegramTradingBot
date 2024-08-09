package presentation.telegram.settings.callbackButtons.indicators.bollingerBands

import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.settings.DISABLE_SETTING
import presentation.telegram.settings.ENABLE_SETTING

object SwitchBbDefaultCallbackButton : CallbackButton("", "switchBbDefault") {
    fun getText(enabled: Boolean): String {
        return when (enabled) {
            true -> DISABLE_SETTING
            false -> ENABLE_SETTING
        } + " ПБ"
    }
}