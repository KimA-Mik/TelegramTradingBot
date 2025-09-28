package presentation.telegram

import domain.common.PATH_SEPARATOR
import domain.user.usecase.FindUserUseCase
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.RegisterUserUseCase
import domain.user.usecase.UserToRootUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.merge
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen

class BotModel(
    private val rootTextModel: RootTextModel,
    private val callbackHandler: CallbackHandler,
    private val registerUser: RegisterUserUseCase,
    private val findUser: FindUserUseCase,
    private val userToRoot: UserToRootUseCase,
    private val popUser: PopUserUseCase,
    private val updateHandler: UpdateHandler,
) {

    private val _outMessages = MutableSharedFlow<BotScreen>()
    val outMessages = merge(
        _outMessages,
        callbackHandler.outFlow,
        updateHandler.outScreens
    )

    suspend fun dispatchStartMessage(sender: Long) {
        val registered = when (val result = registerUser(sender)) {
            result.isSuccess -> {}
            else -> ErrorScreen(
                userId = sender,
                error = result.exceptionOrNull()?.let { UiError.TextError(it.localizedMessage) }
                    ?: UiError.UnknownError)
//            is Resource.Success -> Greeting(id = sender)
//            is Resource.Error -> ErrorScreen(id = sender, message = result.message ?: UNKNOWN_ERROR)
        }
        _outMessages.emit(registered)
        _outMessages.emit(Root(id = sender))
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val userResource = findUser(id)
        val user = if (userResource.isSuccess) {
            userResource.getOrNull()!!
        } else {
            val screen = ErrorScreen(
                id,
                "Похоже мне стерли память и я вас не помню, напишите команду /start, чтобы я вас записал."
            )
            _outMessages.emit(screen)
            return
        }

        var path = user.path.split(PATH_SEPARATOR).drop(1)
        val screens = when (text) {
            DefaultCommands.Root.text -> {
                userToRoot(user)
                flowOf(Root(user.id))
            }

            DefaultCommands.Pop.text -> {
                if (path.isNotEmpty()) {
                    popUser(user)
                    path = path.dropLast(1)
                }
                rootTextModel.executeCommand(user, path, String())
            }

            else -> rootTextModel.executeCommand(user, path, text)
        }

        _outMessages.emitAll(screens)
    }

    suspend fun handleCallbackButton(
        callbackData: String,
        userId: Long,
        messageId: Long,
        messageText: String
    ) {
        callbackHandler.handleCallback(callbackData, userId, messageId, messageText)
    }

    companion object {
        private const val UNKNOWN_ERROR = "Неизвестная ошибка"
    }
}