package presentation.telegram.settings.screens.indicators

import presentation.telegram.screens.BotScreen
import presentation.telegram.settings.IndicatorType
import presentation.telegram.settings.stateToText

class IndicatorRetestedScreen(id: Long, newValue: Boolean, indicatorType: IndicatorType) : BotScreen(id) {
    override val text =
        "${indicatorType.text}: уведомления по умолчанию ${stateToText(newValue)}"
}