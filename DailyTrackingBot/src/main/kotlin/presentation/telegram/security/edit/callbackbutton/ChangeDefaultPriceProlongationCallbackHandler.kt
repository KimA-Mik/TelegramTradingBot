package presentation.telegram.security.edit.callbackbutton

import domain.user.model.User
import domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.edit.screen.EditPriceHeader
import kotlin.time.ExperimentalTime

class ChangeDefaultPriceProlongationCallbackHandler(
    private val repository: UserRepository
) : CallbackButtonHandler {
    @OptIn(ExperimentalTime::class)
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = ChangeDefaultPriceProlongationCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton, messageId))
            return@flow
        }

        val newUser = repository.updateUser(user.copy(defaultPriceProlongation = callbackData.newPriceProlongation))
        if (newUser == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return@flow
        }

        emit(EditPriceHeader(newUser, messageId))
    }
}