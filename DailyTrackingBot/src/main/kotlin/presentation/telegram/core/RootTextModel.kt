package presentation.telegram.core

import domain.user.model.User
import domain.user.usecase.NavigateUserUseCase
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.SecurityTextModel

class RootTextModel(
    private val navigateUser: NavigateUserUseCase,
    private val securityTextModel: SecurityTextModel
) : TextModel {
    override val node = NavigationRoot
    private val textModels = mapOf<String, TextModel>()
    private val navigationCommands = mapOf<String, TextModel>()

    override fun executeCommand(user: User, path: List<String>, command: String) = flow<BotScreen> {
        navigateUser(user, NavigationRoot.Security.destination)
//            emitAll(securitiesModel.executeCommand(user, emptyList(), String()))
    }
}