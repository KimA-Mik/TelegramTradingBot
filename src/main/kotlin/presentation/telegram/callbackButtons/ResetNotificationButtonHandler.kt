package presentation.telegram.callbackButtons

import domain.user.useCase.ResetNotificationUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.FuturePriceUpdate

class ResetNotificationButtonHandler(
    private val resetNotification: ResetNotificationUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(userId, "")

        val screenState = when (resetNotification(userId, ticker)) {
            true -> FuturePriceUpdate.State.ResetNotify(messageText)
            false -> FuturePriceUpdate.State.UnableResetNotify(messageText)
        }

        return FuturePriceUpdate(
            userId = userId,
            messageId = messageId,
            state = screenState
        )
    }
}