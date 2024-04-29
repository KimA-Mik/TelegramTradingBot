package presentation.telegram.textModels

import domain.user.model.User
import domain.user.navigation.useCase.NavigateUserUseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import presentation.telegram.BotTextCommands
import presentation.telegram.common.UNKNOWN_COMMAND
import presentation.telegram.common.UNKNOWN_PATH
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.Root
import presentation.telegram.textModels.common.TextModel

class RootTextModel(
    mySecuritiesTextModel: MySecuritiesTextModel,
    searchSecuritiesTextModel: SearchSecuritiesTextModel,
    settingsTextModel: SettingsTextModel,
    private val navigateUser: NavigateUserUseCase
) : TextModel {
    private val textModels = mapOf(
        BotTextCommands.MySecurities.name to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.name to searchSecuritiesTextModel,
        BotTextCommands.Settings.name to settingsTextModel
    )

    private val navigationCommands = mapOf(
        BotTextCommands.MySecurities.text to mySecuritiesTextModel,
        BotTextCommands.SearchSecurities.text to searchSecuritiesTextModel,
        BotTextCommands.Settings.text to settingsTextModel
    )

    override fun executeCommand(user: User, path: List<String>, command: String) = flow<BotScreen> {
        if (command.isBlank()) {
            emit(Root(user.id))
            return@flow
        }

        if (path.isEmpty()) {
            emitAll(command(user, command))
        } else {
            emitAll(passExecution(user, path, command))
        }
    }

    private fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ) = flow<BotScreen> {
        val nextScreen = path.first()

        textModels[nextScreen]?.let {
            emitAll(it.executeCommand(user = user, path = path.drop(1), command = command))
            return@flow
        }
        emit(ErrorScreen(user.id, UNKNOWN_PATH))
    }

    private fun command(user: User, command: String) = flow<BotScreen> {
        navigationCommands[command]?.let { model ->
            val destination = BotTextCommands.entries.find { it.text == command }?.name
            if (destination == null) {
                emit(ErrorScreen(user.id, UNKNOWN_COMMAND))
                return@flow
            }

            navigateUser(user, destination)
            emitAll(model.executeCommand(user, emptyList(), String()))
            return@flow
        }
        emit(ErrorScreen(user.id, UNKNOWN_COMMAND))
    }
}