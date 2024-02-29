package presentation.telegram.textModels

import domain.user.model.User
import domain.user.navigation.useCase.NavigateUserUseCase
import presentation.telegram.BotScreen
import presentation.telegram.BotTextCommands
import presentation.telegram.textModels.common.TextModel
import presentation.telegram.textModels.common.UNKNOWN_COMMAND
import presentation.telegram.textModels.common.UNKNOWN_PATH

class RootTextModel(
    mySecuritiesTextModel: MySecuritiesTextModel,
    searchSecuritiesTextModel: SearchSecuritiesTextModel,
    private val navigateUser: NavigateUserUseCase
) : TextModel {
    private val textModels = mapOf(
        BotTextCommands.MySecurities.name to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.name to searchSecuritiesTextModel
    )

    private val navigationCommands = mapOf(
        BotTextCommands.MySecurities.text to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.text to searchSecuritiesTextModel
    )

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        if (command.isBlank()) {
            return BotScreen.Root(user.id)
        }

        return if (path.size == 1) {
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
        navigateUser(user, destination)
        return model.executeCommand(user, emptyList(), String())
    }
}