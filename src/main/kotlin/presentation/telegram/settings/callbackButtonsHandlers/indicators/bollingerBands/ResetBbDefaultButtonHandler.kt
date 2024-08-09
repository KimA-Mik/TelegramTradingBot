package presentation.telegram.settings.callbackButtonsHandlers.indicators.bollingerBands

import domain.user.model.User
import domain.user.useCase.indicators.bollingerBands.ResetBbDefaultUseCase
import domain.user.useCase.indicators.result.ResetIndicatorResult
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.settings.IndicatorType
import presentation.telegram.settings.NO_SHARES_TO_SET
import presentation.telegram.settings.screens.indicators.IndicatorRetestedScreen

class ResetBbDefaultButtonHandler(private val resetBbDefault: ResetBbDefaultUseCase) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        return when (val res = resetBbDefault(user)) {
            ResetIndicatorResult.NoShares -> ErrorScreen(user.id, NO_SHARES_TO_SET)
            is ResetIndicatorResult.Success -> IndicatorRetestedScreen(
                id = user.id,
                newValue = res.newValue,
                indicatorType = IndicatorType.BOLLINGER_BANDS
            )
        }
    }
}

