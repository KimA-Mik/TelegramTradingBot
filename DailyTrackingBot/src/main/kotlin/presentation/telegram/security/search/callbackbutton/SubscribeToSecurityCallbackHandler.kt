package presentation.telegram.security.search.callbackbutton

import domain.user.model.User
import domain.user.usecase.SubscribeToSecurityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.search.screen.TickerSearchResultScreen

class SubscribeToSecurityCallbackHandler(
    private val subscribeToSecurity: SubscribeToSecurityUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = SubscribeToSecurityCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val result = subscribeToSecurity(user, callbackData.ticker)
        emit(TickerSearchResultScreen(user.id, messageId, result))
    }
}