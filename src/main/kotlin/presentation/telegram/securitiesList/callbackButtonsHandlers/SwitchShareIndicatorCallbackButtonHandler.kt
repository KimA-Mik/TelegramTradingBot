package presentation.telegram.securitiesList.callbackButtonsHandlers

import domain.user.model.User
import domain.user.useCase.indicators.SwitchShareIndicatorUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.callbackButtons.UNABLE_TO_PARSE_CALLBACK_DATA
import presentation.telegram.callbackButtons.UNKNOWN_BUTTON_ERROR
import presentation.telegram.common.NOT_SUBSCRIBED_TO_SHARE
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.securitiesList.callbackButtons.SwitchShareIndicatorCallbackButton
import presentation.telegram.securitiesList.screens.EditShareScreen

class SwitchShareIndicatorCallbackButtonHandler(
    private val switchShareIndicatorUseCase: SwitchShareIndicatorUseCase
) : CallbackButtonHandler {
    override suspend fun execute(user: User, messageId: Long, messageText: String, arguments: List<String>): BotScreen {
        val callbackData = SwitchShareIndicatorCallbackButton.parseCallbackData(arguments)
            ?: return ErrorScreen(user.id, UNABLE_TO_PARSE_CALLBACK_DATA)

        return when (val res = switchShareIndicatorUseCase(
            userId = user.id,
            ticker = callbackData.ticker,
            indicator = callbackData.indicatorType.toIndicator(),
            newValue = callbackData.newState
        )) {
            SwitchShareIndicatorUseCase.Result.Error -> ErrorScreen(id = user.id, message = UNKNOWN_BUTTON_ERROR)
            SwitchShareIndicatorUseCase.Result.NotSubscribed -> ErrorScreen(
                id = user.id,
                message = NOT_SUBSCRIBED_TO_SHARE + callbackData.ticker
            )

            is SwitchShareIndicatorUseCase.Result.Success -> EditShareScreen(
                id = user.id,
                messageId = messageId,
                state = EditShareScreen.State.Share(res.userShare)
            )
        }
    }
}