package presentation.telegram.settings.root.callbackbutton

import domain.user.model.User
import domain.user.usecase.ChangeTimeframesToFireUseCase
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.settings.root.screen.SettingsRootScreen

class ChangeTimeframesToFireCallbackButtonHandler(
    private val changeTimeframesToFire: ChangeTimeframesToFireUseCase
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ) = flow {
        val callbackData = ChangeTimeframesToFireCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        changeTimeframesToFire(user, callbackData.direction)?.let { updatedUser ->
            emit(SettingsRootScreen(updatedUser, messageId))
        } ?: run {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
        }
    }
}