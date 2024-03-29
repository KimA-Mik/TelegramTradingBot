package presentation.telegram

import Resource
import domain.common.PATH_SEPARATOR
import domain.user.navigation.useCase.PopUserUseCase
import domain.user.navigation.useCase.RegisterUserUseCase
import domain.user.navigation.useCase.UserToRootUseCase
import domain.user.useCase.FindUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.Greeting
import presentation.telegram.screens.Root
import presentation.telegram.textModels.RootTextModel


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
            is Resource.Success -> Greeting(id = sender)
            is Resource.Error -> ErrorScreen(id = sender, message = result.message ?: UNKNOWN_ERROR)
        }
        _outMessages.emit(registered)
        _outMessages.emit(Root(id = sender))
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val userResource = findUser(id)
        val user = if (userResource is Resource.Success) {
            userResource.data!!
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
            BotTextCommands.Root.text -> {
                userToRoot(user)
                flowOf(Root(user.id))
            }

            BotTextCommands.Pop.text -> {
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

    suspend fun handleCallbackButton(callbackData: String, userId: Long, messageId: Long, messageText: String) {
        callbackHandler.handleCallback(callbackData, userId, messageId, messageText)
    }

    companion object {
        private const val UNKNOWN_ERROR = "Неизвестная ошибка"
    }
}