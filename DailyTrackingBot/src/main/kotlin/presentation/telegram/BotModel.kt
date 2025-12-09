package presentation.telegram

import com.github.kotlintelegrambot.types.TelegramBotResult
import domain.common.PATH_SEPARATOR
import domain.user.usecase.FindUserUseCase
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.RegisterUserUseCase
import domain.user.usecase.UserToRootUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import presentation.telegram.core.DefaultCommands
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.core.screen.Greeting
import presentation.telegram.core.screen.Root

class BotModel(
    private val rootTextModel: RootTextModel,
    private val callbackHandler: CallbackHandler,
    private val registerUser: RegisterUserUseCase,
    private val findUser: FindUserUseCase,
    private val userToRoot: UserToRootUseCase,
    private val popUser: PopUserUseCase,
    private val updateHandler: UpdateHandler,
    private val errorHandler: MessageErrorHandler
) {

    private val _outMessages = MutableSharedFlow<BotScreen>()
    val outMessages = merge(
        _outMessages,
        callbackHandler.outFlow,
        updateHandler.outScreens,
        errorHandler.outMessages
    )

    suspend fun homeCommand(userId: Long) {
        val userResource = findUser(userId)
        val user = if (userResource.isSuccess) {
            userResource.getOrNull()!!
        } else {
            val screen = ErrorScreen(userId, UiError.UnregisteredUserError)
            _outMessages.emit(screen)
            return
        }

        userToRoot(user)
        _outMessages.emit(Root(user.id))
    }

    suspend fun dispatchStartMessage(sender: Long) {
        val result = registerUser(sender)
        val registered = if (result.isSuccess) {
            Greeting(sender)
        } else {
            ErrorScreen(
                userId = sender,
                error = result.exceptionOrNull()?.message?.let { UiError.TextError(it) }
                    ?: UiError.UnknownError
            )
        }

        _outMessages.emit(registered)
        _outMessages.emit(Root(sender))
    }

    suspend fun handleTextInput(id: Long, text: String) {
        val userResource = findUser(id)
        val user = if (userResource.isSuccess) {
            userResource.getOrNull()!!
        } else {
            val screen = ErrorScreen(id, UiError.UnregisteredUserError)
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
                var poppedUser = user
                if (path.isNotEmpty()) {
                    popUser(user).onSuccess {
                        poppedUser = it
                        path = path.dropLast(1)
                    }
                }
                rootTextModel.executeCommand(poppedUser, path, "")
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

    suspend fun handleMessageError(screen: BotScreen, error: TelegramBotResult.Error) =
        errorHandler.handleError(screen, error)

    suspend fun handleMessageException(screen: BotScreen, exception: Exception) =
        errorHandler.handleException(screen, exception)
}