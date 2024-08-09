package presentation.telegram.settings.callbackButtonsHandlers.indicators.rsi

import domain.user.model.User
import domain.user.useCase.indicators.result.ResetIndicatorResult
import domain.user.useCase.indicators.rsi.ResetRsiDefaultUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.IndicatorType
import presentation.telegram.settings.NO_SHARES_TO_SET
import presentation.telegram.settings.screens.indicators.IndicatorRetestedScreen

class ResetRsiDefaultButtonHandler(private val resetRsiDefault: ResetRsiDefaultUseCase) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        return when (val res = resetRsiDefault(user)) {
            ResetIndicatorResult.NoShares -> ErrorScreen(user.id, NO_SHARES_TO_SET)
            is ResetIndicatorResult.Success -> IndicatorRetestedScreen(user.id, res.newValue, IndicatorType.RSI)
        }

    }
}