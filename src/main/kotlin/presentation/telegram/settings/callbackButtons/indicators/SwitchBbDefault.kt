package presentation.telegram.settings.callbackButtons.indicators

import presentation.telegram.callbackButtons.CallbackButton
import presentation.telegram.settings.DISABLE_SETTING
import presentation.telegram.settings.ENABLE_SETTING

object SwitchBbDefault : CallbackButton("", "switchBbDefault") {
    fun getText(enabled: Boolean): String {
        return when (enabled) {
            true -> DISABLE_SETTING
            false -> ENABLE_SETTING
        } + " ПБ"
    }
}