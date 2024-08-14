package presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi

import domain.user.model.User
import domain.user.useCase.indicators.result.SwitchIndicatorDefaultResult
import domain.user.useCase.indicators.rsi.SwitchRsiDefaultUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.UNABLE_TO_SWITCH_DEFAULT
import presentation.telegram.settings.screens.indicators.IndicatorsSettings

class SwitchRsiDefaultButtonHandler(private val switchRsiDefault: SwitchRsiDefaultUseCase) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        return when (val res = switchRsiDefault(user)) {
            SwitchIndicatorDefaultResult.Error -> ErrorScreen(user.id, UNABLE_TO_SWITCH_DEFAULT)
            is SwitchIndicatorDefaultResult.Success -> IndicatorsSettings(res.user, messageId)
        }
    }
}