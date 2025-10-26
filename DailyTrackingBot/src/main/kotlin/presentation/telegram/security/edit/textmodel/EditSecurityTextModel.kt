package presentation.telegram.security.edit.textmodel

import domain.user.model.User
import domain.user.repository.UserRepository
import domain.user.usecase.NavigateUserUseCase
import kotlinx.coroutines.flow.*
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.edit.screen.EditSecurityHeader
import presentation.telegram.security.edit.screen.SecurityScreen
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest

class EditSecurityTextModel(
    private val api: CacheServerApi,
    private val navigateUser: NavigateUserUseCase,
    private val repository: UserRepository,
    editPriceTextModel: EditPriceTextModel,
    editLowPriceTextModel: EditLowPriceTextModel,
    editPercentTextModel: EditPercentTextModel,
    editNoteTextModel: EditNoteTextModel
) : TextModel {
    override val node = NavigationRoot.SecurityList.SecurityDetails
    private val textModels = mapOf(
        editNoteTextModel.node.destination to editNoteTextModel,
        editPercentTextModel.node.destination to editPercentTextModel,
        editPriceTextModel.node.destination to editPriceTextModel,
        editLowPriceTextModel.node.destination to editLowPriceTextModel,
    )
    private val navigationCommands = mapOf<String, TextModel>(
        EditSecurityHeader.Command.Notes.text to editNoteTextModel,
        EditSecurityHeader.Command.Price.text to editPriceTextModel,
        EditSecurityHeader.Command.LowPrice.text to editLowPriceTextModel,
        EditSecurityHeader.Command.Percent.text to editPercentTextModel,
    )

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (path.isEmpty()) {
            if (command.isBlank()) {
                emitScreens(user)
            } else {
                emitAll(command(user, command))
            }
        } else {
            emitAll(passExecution(user, path, command))
        }
    }

    private suspend fun FlowCollector<BotScreen>.emitScreens(
        user: User
    ) {
        val fullUser = repository.findFullUserById(user.id)
        if (fullUser == null) {
            emit(ErrorScreen(user.id, UiError.UnregisteredUserError))
            return
        }

        val ticker = user.pathList.last()
        val security = fullUser.securities.firstOrNull { it.ticker == ticker }
        if (security == null) {
            val result = api.findSecurity(ticker)
            if (result is FindSecurityResponse.Share || result is FindSecurityResponse.Future) {
                emit(ErrorScreen(user.id, UiError.UnsubscribedToSecurity(ticker)))
            } else {
                emit(ErrorScreen(user.id, UiError.UnableToLoadSecurity))
            }
            return
        }
        val lastPrice = security.let { api.getOrderBook(GetOrderBookRequest(it.uid)).getOrNull()?.lastPrice }
        emit(EditSecurityHeader(user.id))
        emit(SecurityScreen(user, security, lastPrice))
    }

    private fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ) = flow {
        val nextScreen = path.first()
        textModels[nextScreen]?.let {
            emitAll(it.executeCommand(user = user, path = path.drop(1), command = command))
            return@flow
        }
        emit(ErrorScreen(user.id, UiError.UnknownPath))
    }

    private suspend fun command(user: User, command: String): Flow<BotScreen> {
        navigationCommands[command]?.let { model ->
            return navigateCommand(user, model.node.destination, model)
        }

        return flowOf(ErrorScreen(user.id, UiError.UnknownCommand(command)))
    }

    private suspend fun navigateCommand(
        user: User,
        destination: String,
        model: TextModel
    ): Flow<BotScreen> {
        val u = navigateUser(user, destination).getOrNull() ?: return flow {}
        return model.executeCommand(u, emptyList(), String())
    }
}