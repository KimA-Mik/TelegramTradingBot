package presentation.telegram.core

import domain.user.model.User
import domain.user.usecase.NavigateUserUseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.core.screen.Root
import presentation.telegram.security.textmodel.SecurityTextModel

class RootTextModel(
    private val navigateUser: NavigateUserUseCase,
    private val securityTextModel: SecurityTextModel
) : TextModel {
    override val node = NavigationRoot
    private val textModels = mapOf<String, TextModel>(
        NavigationRoot.Security.destination to securityTextModel
    )
    private val navigationCommands = mapOf<String, TextModel>(
        Root.Commands.Security.text to securityTextModel
    )

    override fun executeCommand(user: User, path: List<String>, command: String) = flow {
        if (path.isEmpty()) {
            if (command.isBlank()) {
                emit(Root(user.id))
            } else {
                emitAll(command(user, command))
            }
        } else {
            emitAll(passExecution(user, path, command))
        }
    }


    private fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ) = flow {
        val nextScreen = path.first()

        textModels[nextScreen]?.let {
            emitAll(it.executeCommand(user = user, path = path.drop(1), command = command))
            return@flow
        }
        emit(ErrorScreen(user.id, UiError.UnknownPath))
    }

    private fun command(user: User, command: String) = flow {
        navigationCommands[command]?.let { model ->
            navigateUser(user, model.node.destination)
            emitAll(model.executeCommand(user, emptyList(), String()))
            return@flow
        }
        emit(ErrorScreen(user.id, UiError.UnknownCommand(command)))
    }
}