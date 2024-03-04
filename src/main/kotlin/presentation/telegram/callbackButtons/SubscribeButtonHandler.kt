package presentation.telegram.callbackButtons

import Resource
import domain.user.useCase.SubscribeUserToShareUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SecuritySearchResult

class SubscribeButtonHandler(
    private val subscribeUserToShare: SubscribeUserToShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(userId: Long, messageId: Long, messageText: String): BotScreen {
        val ticker = messageText.split(' ').firstOrNull() ?: return ErrorScreen(userId, UNKNOWN_BUTTON_ERROR)

        val resource = subscribeUserToShare(userId, ticker)

        return when (resource) {
            is Resource.Error -> ErrorScreen(userId, UNABLE_TO_SUBSCRIBE)
            is Resource.Success -> SecuritySearchResult(
                id = userId, messageId = messageId,
                state = SecuritySearchResult.State.FollowUpdate(
                    followed = true,
                    messageText = messageText
                )
            )
        }
    }
}