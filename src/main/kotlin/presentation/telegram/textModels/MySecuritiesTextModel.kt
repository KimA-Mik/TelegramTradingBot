package presentation.telegram.textModels

import domain.user.model.User
import domain.user.useCase.GetUserSharesUseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import presentation.telegram.common.UNKNOWN_COMMAND
import presentation.telegram.common.UNKNOWN_PATH
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.MySecuritiesList
import presentation.telegram.screens.MySecuritiesRoot
import presentation.telegram.textModels.common.TextModel

class MySecuritiesTextModel(
    private val getUserShares: GetUserSharesUseCase
) : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override fun executeCommand(user: User, path: List<String>, command: String) = flow {
        if (command.isBlank()) {
            emit(MySecuritiesRoot(user.id))
            val screen = when (val result = getUserShares(user.id, 1)) {
                GetUserSharesUseCase.GetUserSharesResult.NotFound -> MySecuritiesList(user.id)
                is GetUserSharesUseCase.GetUserSharesResult.Success -> MySecuritiesList(
                    id = user.id,
                    shares = result.shares,
                    page = result.page,
                    pageSize = result.pageSize,
                    totalPages = result.totalPages
                )
            }

            emit(screen)
            return@flow
        }

        if (path.isEmpty()) {
            emit(command(user, command))
        } else {
            emitAll(passExecution(user, path, command))
        }
    }

    private suspend fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ) = flow {
        val nextScreen = path.first()
        textModels[nextScreen]?.let {
            emitAll(
                it.executeCommand(
                    user = user,
                    path = path.drop(1),
                    command = command
                )
            )
            return@flow
        }
        emit(ErrorScreen(user.id, UNKNOWN_PATH))
    }

    private fun command(user: User, command: String): BotScreen {
        navigationCommands[command]?.let {
            return navigateCommand(user, command, it)
        }

        return ErrorScreen(user.id, UNKNOWN_COMMAND)
    }

    private fun navigateCommand(user: User, destination: String, model: TextModel): BotScreen {
//        navigateUser(user, destination)
//        return model.executeCommand(user, emptyList(), String())
        return ErrorScreen(user.id, UNKNOWN_PATH)
    }
}