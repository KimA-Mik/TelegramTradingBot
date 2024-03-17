package presentation.telegram.callbackButtons

import domain.user.useCase.ChangeUserSharePercentUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.EditShareScreen
import presentation.telegram.screens.ErrorScreen

class SharePercentButtonHandler(
    private val changeUserSharePercent: ChangeUserSharePercentUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(userId, UNABLE_TO_CHANGE_SHARE_PERCENT)

        val change = arguments
            .getOrNull(1)
            ?.toDoubleOrNull()
            ?: return ErrorScreen(userId, UNABLE_TO_CHANGE_SHARE_PERCENT)

        return when (val res = changeUserSharePercent(userId, ticker, change)) {
            is ChangeUserSharePercentUseCase.ChangeUserSharePercentResult.NotSubscribed ->
                EditShareScreen(
                    id = userId,
                    messageId = messageId,
                    state = EditShareScreen.State.NotSubscribed(res.ticker)
                )

            is ChangeUserSharePercentUseCase.ChangeUserSharePercentResult.Success -> EditShareScreen(
                id = userId,
                messageId = messageId,
                state = EditShareScreen.State.Share(res.share.ticker, res.share.percent)
            )
        }
    }
}