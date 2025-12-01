package presentation.telegram.settings.root.callbackbutton

import domain.user.model.User
import domain.user.usecase.ToggleSrsiAlertUseCase
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.settings.root.screen.SettingsRootScreen

class ToggleSrsiAlertCallbackButtonHandler(
    private val toggleSrsiAlert: ToggleSrsiAlertUseCase,
) : CallbackButtonHandler {
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ) = flow {
        val callbackData = ToggleSrsiAlertCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        val updatedUser = toggleSrsiAlert(user, callbackData.newValue)
        if (updatedUser == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return@flow
        }

        emit(SettingsRootScreen(updatedUser, messageId))
    }
}