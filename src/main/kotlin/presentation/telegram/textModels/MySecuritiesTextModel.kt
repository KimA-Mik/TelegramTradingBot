package presentation.telegram.textModels

import domain.user.model.User
import presentation.telegram.common.UNKNOWN_COMMAND
import presentation.telegram.common.UNKNOWN_PATH
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.MySecurities
import presentation.telegram.textModels.common.TextModel

class MySecuritiesTextModel : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        if (command.isBlank()) {
            return MySecurities(user.id)
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