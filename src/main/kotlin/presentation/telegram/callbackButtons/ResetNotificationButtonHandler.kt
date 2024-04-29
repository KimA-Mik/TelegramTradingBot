package presentation.telegram.callbackButtons

import domain.user.model.User
import domain.user.useCase.ResetNotificationUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.FuturePriceUpdate

class ResetNotificationButtonHandler(
    private val resetNotification: ResetNotificationUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.firstOrNull()
            ?: return ErrorScreen(user.id, BROKEN_BUTTON)

        val screenState = when (resetNotification(user.id, ticker)) {
            true -> FuturePriceUpdate.State.ResetNotify(messageText)
            false -> FuturePriceUpdate.State.UnableResetNotify(messageText)
        }

        return FuturePriceUpdate(
            userId = user.id,
            messageId = messageId,
            state = screenState
        )
    }
}