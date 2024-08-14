package presentation.telegram.securitiesList.callbackButtonsHandlers

import domain.user.model.User
import domain.user.useCase.GetUserShareUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.callbackButtons.UNABLE_TO_EDIT_SHARE
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.securitiesList.screens.EditShareScreen

class EditShareButtonHandler(
    private val getUserShare: GetUserShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(user.id, UNABLE_TO_EDIT_SHARE)

        return when (val result = getUserShare(user.id, ticker)) {
            is GetUserShareUseCase.GetUserShareResult.NotFound -> EditShareScreen(
                id = user.id,
                state = EditShareScreen.State.NotSubscribed(result.ticker)
            )

            is GetUserShareUseCase.GetUserShareResult.Success -> EditShareScreen(
                id = user.id,
                state = EditShareScreen.State.Share(result.share.ticker, result.share.percent)
            )
        }
    }
}