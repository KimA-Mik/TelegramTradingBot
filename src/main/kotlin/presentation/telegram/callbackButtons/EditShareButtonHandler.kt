package presentation.telegram.callbackButtons

import domain.user.useCase.GetUserShareUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.EditShareScreen
import presentation.telegram.screens.ErrorScreen

class EditShareButtonHandler(
    private val getUserShare: GetUserShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(userId, UNABLE_TO_EDIT_SHARE)

        return when (val result = getUserShare(userId, ticker)) {
            is GetUserShareUseCase.GetUserShareResult.NotFound -> EditShareScreen(
                id = userId,
                state = EditShareScreen.State.NotSubscribed(result.ticker)
            )

            is GetUserShareUseCase.GetUserShareResult.Success -> EditShareScreen(
                id = userId,
                state = EditShareScreen.State.Share(result.share.ticker, result.share.percent)
            )
        }
    }
}