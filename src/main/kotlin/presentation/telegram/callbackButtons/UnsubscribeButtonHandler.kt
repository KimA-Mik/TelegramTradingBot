package presentation.telegram.callbackButtons

import Resource
import domain.user.useCase.UnsubscribeUserFromShareUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SecuritySearchResult

class UnsubscribeButtonHandler(
    private val unsubscribeUserToShare: UnsubscribeUserFromShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(userId: Long, messageId: Long, messageText: String): BotScreen {
        val ticker = messageText.split(' ').firstOrNull() ?: return ErrorScreen(userId, UNKNOWN_BUTTON_ERROR)

        val resource = unsubscribeUserToShare(userId, ticker)

        return when (resource) {
            is Resource.Error -> ErrorScreen(userId, UNABLE_TO_UNSUBSCRIBE)
            is Resource.Success -> SecuritySearchResult(
                id = userId, messageId = messageId,
                state = SecuritySearchResult.State.FollowUpdate(
                    followed = false,
                    messageText = messageText
                )
            )
        }
    }
}