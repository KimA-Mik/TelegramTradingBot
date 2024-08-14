package presentation.telegram.securitiesList.callbackButtonsHandlers

import domain.user.model.User
import domain.user.useCase.ChangeUserSharePercentUseCase
import presentation.telegram.callbackButtons.CallbackButtonHandler
import presentation.telegram.callbackButtons.UNABLE_TO_CHANGE_SHARE_PERCENT
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.securitiesList.screens.EditShareScreen

class SharePercentButtonHandler(
    private val changeUserSharePercent: ChangeUserSharePercentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(user.id, UNABLE_TO_CHANGE_SHARE_PERCENT)

        val change = arguments
            .getOrNull(1)
            ?.toDoubleOrNull()
            ?: return ErrorScreen(user.id, UNABLE_TO_CHANGE_SHARE_PERCENT)

        return when (val res = changeUserSharePercent(user.id, ticker, change)) {
            is ChangeUserSharePercentUseCase.ChangeUserSharePercentResult.NotSubscribed ->
                EditShareScreen(
                    id = user.id,
                    messageId = messageId,
                    state = EditShareScreen.State.NotSubscribed(res.ticker)
                )

            is ChangeUserSharePercentUseCase.ChangeUserSharePercentResult.Success -> EditShareScreen(
                id = user.id,
                messageId = messageId,
                state = EditShareScreen.State.Share(res.share)
            )
        }
    }
}