package presentation.telegram.textModels

import domain.tinkoff.useCase.GetFullSecurityUseCase
import domain.user.model.User
import domain.user.useCase.IsUserSubscribedUseCase
import presentation.telegram.screens.*
import presentation.telegram.textModels.common.TextModel
import presentation.telegram.textModels.common.UNKNOWN_PATH

class SearchSecuritiesTextModel(
    private val getFullSecurityUseCase: GetFullSecurityUseCase,
    private val isUserSubscribed: IsUserSubscribedUseCase
) : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        if (command.isBlank()) {
            return SearchSecurities(user.id)
        }

        return if (path.isEmpty()) {
            command(user, command)
        } else {
            passExecution(user, path, command)
        }
    }

    private suspend fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ): BotScreen {
        val nextScreen = path.first()

        return if (textModels.containsKey(nextScreen)) {
            return textModels[nextScreen]!!.executeCommand(
                user = user,
                path = path.drop(1),
                command = command
            )
        } else {
            ErrorScreen(user.id, UNKNOWN_PATH)
        }
    }

    private suspend fun command(user: User, command: String): BotScreen {
        navigationCommands[command]?.let {
            return navigateCommand(user, command, it)
        }

        return customCommand(user, command)
    }

    private suspend fun customCommand(user: User, command: String): BotScreen {
        return when (val result = getFullSecurityUseCase(command)) {
            GetFullSecurityUseCase.GetSecurityResult.SecurityNotFound -> SecurityNotFound(user.id, command)
            is GetFullSecurityUseCase.GetSecurityResult.Success -> SecuritySearchResult(
                id = user.id, messageId = null,
                state = SecuritySearchResult.State.SearchResult(
                    result = result.result,
                    followed = isUserSubscribed(user.id, command)
                )
            )
        }
    }

    private fun navigateCommand(user: User, destination: String, model: TextModel): BotScreen {
//        navigateUser(user, destination)
//        return model.executeCommand(user, emptyList(), String())
        return ErrorScreen(user.id, UNKNOWN_PATH)
    }
}