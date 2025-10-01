package presentation.telegram.security.textmodel

import domain.common.PATH_SEPARATOR
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
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
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

        if (command == EditTickerScreen.Commands.Cancel.text) {
            popUser(user)
                .onFailure {
                    emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
                    return@flow
                }
                .onSuccess {
                    emitAll(rootTextModel.executeCommand(it, it.path.split(PATH_SEPARATOR), ""))
                    return@flow
                }
        }

        val searchResult = findSecurity(command)
        emit(TickerSearchResultScreen(user.id, searchResult = searchResult))

        if (searchResult is FindSecurityUseCase.Result.Success) {
            val newUser = updateTicker(user, searchResult.security.ticker)
            emitAll(rootTextModel.executeCommand(newUser, newUser.path.split(PATH_SEPARATOR), ""))
        }
    }
}