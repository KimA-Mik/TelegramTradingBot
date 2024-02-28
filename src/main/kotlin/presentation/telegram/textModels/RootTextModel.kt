package presentation.telegram.textModels

import domain.user.model.User
import domain.user.navigation.useCase.NavigateUserUseCase
import presentation.telegram.BotScreen
import presentation.telegram.BotTextCommands

class RootTextModel(
    mySecuritiesTextModel: MySecuritiesTextModel,
    searchSecuritiesTextModel: SearchSecuritiesTextModel,
    private val navigateUser: NavigateUserUseCase
) : TextModel {
    private val textModels = mapOf(
        BotTextCommands.MySecurities.name to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.name to searchSecuritiesTextModel
    )

    private val commands = mapOf(
        BotTextCommands.MySecurities.text to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.text to searchSecuritiesTextModel

    )

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        return if (path.size == 1) {
            executeCommand(user, command, path)
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

    private suspend fun executeCommand(
        user: User,
        command: String,
        path: List<String>
    ): BotScreen {
        return if (commands.containsKey(command)) {
            navigateUser(user, command)
            commands[command]!!.executeCommand(
                user = user,
                path = path.drop(1),
                command = command
            )
        } else {
            BotScreen.Error(user.id, UNKNOWN_COMMAND)
        }
    }
}