package presentation.telegram.textModels

import domain.user.model.User
import domain.user.navigation.useCase.NavigateUserUseCase
import presentation.telegram.BotTextCommands
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.Root
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
            return Root(user.id)
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

        return ErrorScreen(user.id, UNKNOWN_COMMAND)
    }

    private suspend fun navigateCommand(user: User, command: String, model: TextModel): BotScreen {
        val destination = BotTextCommands.entries.find { it.text == command }?.name ?: return ErrorScreen(
            user.id,
            UNKNOWN_COMMAND
        )

        navigateUser(user, destination)
        return model.executeCommand(user, emptyList(), String())
    }
}