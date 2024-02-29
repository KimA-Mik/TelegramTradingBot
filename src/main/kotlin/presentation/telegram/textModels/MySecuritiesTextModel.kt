package presentation.telegram.textModels

import domain.user.model.User
import presentation.telegram.BotScreen
import presentation.telegram.textModels.common.TextModel
import presentation.telegram.textModels.common.UNKNOWN_COMMAND
import presentation.telegram.textModels.common.UNKNOWN_PATH

class MySecuritiesTextModel : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        if (command.isBlank()) {
            return BotScreen.MySecurities(user.id)
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
            BotScreen.Error(user.id, UNKNOWN_PATH)
        }
    }

    private suspend fun command(user: User, command: String): BotScreen {
        navigationCommands[command]?.let {
            return navigateCommand(user, command, it)
        }

        return BotScreen.Error(user.id, UNKNOWN_COMMAND)
    }

    private suspend fun navigateCommand(user: User, destination: String, model: TextModel): BotScreen {
//        navigateUser(user, destination)
//        return model.executeCommand(user, emptyList(), String())
        return BotScreen.Error(user.id, UNKNOWN_PATH)
    }
}