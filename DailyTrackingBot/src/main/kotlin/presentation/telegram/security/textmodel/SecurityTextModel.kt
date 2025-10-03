package presentation.telegram.security.textmodel

import domain.common.PATH_SEPARATOR
import domain.user.model.User
import domain.user.usecase.NavigateUserUseCase
import domain.user.usecase.StartSecurityConfigureSequenceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.screen.SecurityHeader
import presentation.telegram.security.screen.SecurityScreen
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest

class SecurityTextModel(
    private val api: CacheServerApi,
    private val navigateUser: NavigateUserUseCase,
    private val startSecurityConfigureSequence: StartSecurityConfigureSequenceUseCase,
    editPriceTextModel: EditPriceTextModel,
    editPercentTextModel: EditPercentTextModel,
    editTickerTextModel: EditTickerTextModel,
    editNoteTextModel: EditNoteTextModel
) : TextModel {
    override val node = NavigationRoot.Security
    private val textModels = mapOf(
        editNoteTextModel.node.destination to editNoteTextModel,
        editPercentTextModel.node.destination to editPercentTextModel,
        editTickerTextModel.node.destination to editTickerTextModel,
        editPriceTextModel.node.destination to editPriceTextModel
    )
    private val navigationCommands = mapOf(
        SecurityHeader.Command.Notes.text to editNoteTextModel,
        SecurityHeader.Command.Price.text to editPriceTextModel,
        SecurityHeader.Command.Percent.text to editPercentTextModel,
        SecurityHeader.Command.Ticker.text to editTickerTextModel,
    )

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            val security = user.ticker?.let { ticker ->
                when (val res = api.findSecurity(ticker)) {
                    is FindSecurityResponse.Share -> res.share
                    is FindSecurityResponse.Future -> res.future
                    else -> null
                }
            }
            val lastPrice = security?.let { api.getOrderBook(GetOrderBookRequest(it.uid)).getOrNull()?.lastPrice }
            emit(SecurityHeader(user.id))
            emit(SecurityScreen(user, security, lastPrice))
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

        if (command == SecurityHeader.Command.Reconfigure.text) {
            val u = startSecurityConfigureSequence(user) ?: return flowOf()
            val path = u.path.split(PATH_SEPARATOR)
            val nextScreen = path.last()
            textModels[nextScreen]?.let {
                return it.executeCommand(user = user, path = path.drop(1), command = command)
            }
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