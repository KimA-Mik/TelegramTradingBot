package presentation.telegram.textModels

import domain.user.model.User
import kotlinx.coroutines.flow.flow
import presentation.telegram.common.UNKNOWN_COMMAND
import presentation.telegram.screens.BotScreen
import presentation.telegram.screens.ErrorScreen
import presentation.telegram.screens.SettingsDefaultPercent
import presentation.telegram.screens.SettingsRoot
import presentation.telegram.textModels.common.TextModel

class SettingsTextModel : TextModel {
    override fun executeCommand(user: User, path: List<String>, command: String) = flow<BotScreen> {
        if (command.isBlank()) {
            emit(SettingsRoot(user))
            return@flow
        }

        val screen = when (command) {
            SettingsTextCommands.DefaultPercent.text -> SettingsDefaultPercent(user.id, user.defaultPercent)
            else -> ErrorScreen(user.id, UNKNOWN_COMMAND)
        }

        emit(screen)
    }


    enum class SettingsTextCommands(val text: String) {
        DefaultPercent("Стандартный процент")
    }
}