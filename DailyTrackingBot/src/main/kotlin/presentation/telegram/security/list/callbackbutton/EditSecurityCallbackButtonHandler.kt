package presentation.telegram.security.list.callbackbutton

import domain.user.model.User
import domain.user.usecase.NavigateUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.CallbackButtonHandler
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen

class EditSecurityCallbackButtonHandler(
    private val navigateUser: NavigateUserUseCase
) : CallbackButtonHandler {
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)
    override suspend fun execute(
        user: User,
        messageId: Long,
        messageText: String,
        arguments: List<String>
    ): Flow<BotScreen> = flow {
        val callbackData = EditSecurityCallbackButton.parseCallbackData(arguments)
        if (callbackData == null) {
            emit(ErrorScreen(userId = user.id, UiError.BrokenCallbackButton))
            return@flow
        }

        navigateUser.absolute(
            user,
            NavigationRoot.SecurityList.destination,
            callbackData.ticker
        ).onSuccess {
            emitAll(rootTextModel.executeCommand(it, it.pathList, ""))
        }.onFailure {
            emit(ErrorScreen(userId = user.id, UiError.BrokenCallbackButton))
        }
    }
}