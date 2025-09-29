package presentation.telegram.security.textmodel

import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen

class EditPercentTextModel : TextModel {
    override val node = NavigationRoot.Security.EditPercentage

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> {
        TODO("Not yet implemented")
    }
}