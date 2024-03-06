package presentation.telegram.callbackButtons

import Resource
import domain.user.useCase.SubscribeUserToShareUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SecuritySearchResult

class SubscribeButtonHandler(
    private val subscribeUserToShare: SubscribeUserToShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        userId: Long,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.getOrElse(0) { String() }

        val resource = subscribeUserToShare(userId, ticker)

        return when (resource) {
            is Resource.Error -> ErrorScreen(userId, UNABLE_TO_SUBSCRIBE)
            is Resource.Success -> SecuritySearchResult(
                id = userId, messageId = messageId, ticker = ticker,
                state = SecuritySearchResult.State.FollowUpdate(
                    followed = true,
                    messageText = messageText
                )
            )
        }
    }
}