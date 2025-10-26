package presentation.telegram.security.edit.callbackbutton

import domain.user.model.User
import domain.user.usecase.ResetPriceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen

class ResetPriceCallbackHandler(
    private val resetPrice: ResetPriceUseCase,
    private val securityScreenUpdateUserHandler: SecurityScreenUpdateUserHandler
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = ResetPriceCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        securityScreenUpdateUserHandler.handle(
            user, messageId, UiError.UnsubscribedToSecurity(callbackData.ticker)
        ) {
            resetPrice(user, callbackData.ticker)
        }.let { emit(it) }
    }
}