package presentation.telegram.security.list.textmodel

import domain.user.model.User
import domain.user.usecase.GetUserTrackingSecuritiesUseCase
import domain.user.usecase.NavigateUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.UiError
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.core.screen.ErrorScreen
import presentation.telegram.security.list.screen.SecurityHeader
import presentation.telegram.security.list.screen.SecurityListScreen
import ru.kima.cacheserver.api.api.CacheServerApi

class SecurityListTextModel(
    private val api: CacheServerApi,
    private val navigateUser: NavigateUserUseCase,
    private val getUserTrackingSecurities: GetUserTrackingSecuritiesUseCase
) : TextModel {
    override val node = NavigationRoot.SecurityList
    private val textModels = mapOf<String, TextModel>(
//        editNoteTextModel.node.destination to editNoteTextModel,
//        editPercentTextModel.node.destination to editPercentTextModel,
//        editTickerTextModel.node.destination to editTickerTextModel,
//        editPriceTextModel.node.destination to editPriceTextModel
    )
    private val navigationCommands = mapOf<String, TextModel>(
//        SecurityHeader.Command.Notes.text to editNoteTextModel,
//        SecurityHeader.Command.Price.text to editPriceTextModel,
//        SecurityHeader.Command.Percent.text to editPercentTextModel,
//        SecurityHeader.Command.Ticker.text to editTickerTextModel,
    )

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            val screen = when (val securitiesResult = getUserTrackingSecurities(user.id, page = 1)) {
                GetUserTrackingSecuritiesUseCase.GetUserSharesResult.NotFound -> SecurityListScreen(
                    userId = user.id,
                    securities = emptyList(),
                )

                is GetUserTrackingSecuritiesUseCase.GetUserSharesResult.Success -> SecurityListScreen(
                    userId = user.id,
                    securities = securitiesResult.securities,
                    page = securitiesResult.page,
                    pageSize = securitiesResult.pageSize,
                    totalPages = securitiesResult.totalPages
                )
            }

            emit(SecurityHeader(user.id))
            emit(screen)
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
        return flowOf(ErrorScreen(user.id, UiError.UnknownCommand(command)))
        navigationCommands[command]?.let { model ->
            return navigateCommand(user, model.node.destination, model)
        }
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