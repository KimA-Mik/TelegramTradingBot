package presentation.telegram.callbackButtons

import Resource
import domain.user.model.User
import domain.user.useCase.SubscribeUserToShareUseCase
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SecuritySearchResult

class SubscribeButtonHandler(
    private val subscribeUserToShare: SubscribeUserToShareUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): BotScreen {
        val ticker = arguments.getOrElse(0) { String() }
        val resource = subscribeUserToShare(user.id, ticker)

        return when (resource) {
            is Resource.Error -> ErrorScreen(user.id, UNABLE_TO_SUBSCRIBE)
            is Resource.Success -> SecuritySearchResult(
                user = user, messageId = messageId, ticker = ticker,
                state = SecuritySearchResult.State.FollowUpdate(
                    followed = true,
                    messageText = messageText
                )
            )
        }
    }
}