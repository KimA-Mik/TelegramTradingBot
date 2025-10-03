package presentation.telegram.security.callbackbutton

import domain.user.model.User
import domain.user.usecase.UpdateRemainActiveUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen

class ToggleRemainActiveCallbackHandler(
    private val updateRemainActive: UpdateRemainActiveUseCase,
    private val securityScreenUpdateUserHandler: SecurityScreenUpdateUserHandler
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = ToggleRemainActiveCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val newScreen = securityScreenUpdateUserHandler.handle(user, messageId) {
            updateRemainActive(user, callbackData.newValue)
        }
        emit(newScreen)
    }
}