package presentation.telegram.security.textmodel

import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import domain.user.usecase.PopUserUseCase
import domain.user.usecase.UpdateTickerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.RootTextModel
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.screen.EditTickerScreen
import presentation.telegram.security.screen.TickerSearchResultScreen

class EditTickerTextModel(
    private val popUser: PopUserUseCase,
    private val findSecurity: FindSecurityUseCase,
    private val updateTicker: UpdateTickerUseCase
) : TextModel {
    override val node = NavigationRoot.Security.EditTicker
    private val rootTextModel: RootTextModel by inject(RootTextModel::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            emit(EditTickerScreen(user.id))
            return@flow
        }

        val searchResult = runCatching { findSecurity(command) }
            .onFailure { println(it) }
            .getOrDefault(FindSecurityUseCase.Result.NotFound)
        emit(TickerSearchResultScreen(user.id, searchResult = searchResult))

        if (searchResult is FindSecurityUseCase.Result.Success) {
            var newUser = updateTicker(user, searchResult.security.ticker)
            newUser = popUser(newUser).getOrDefault(user)
            emitAll(rootTextModel.executeCommand(newUser, newUser.pathList, ""))
        }
    }
}