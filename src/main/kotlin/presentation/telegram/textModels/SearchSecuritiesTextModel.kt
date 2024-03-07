package presentation.telegram.textModels

import domain.tinkoff.useCase.GetFullSecurityUseCase
import domain.user.model.User
import domain.user.useCase.IsUserSubscribedUseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import presentation.telegram.common.UNKNOWN_PATH
import presentation.telegram.screens.*
import presentation.telegram.textModels.common.TextModel

class SearchSecuritiesTextModel(
    private val getFullSecurityUseCase: GetFullSecurityUseCase,
    private val isUserSubscribed: IsUserSubscribedUseCase,
) : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override fun executeCommand(user: User, path: List<String>, command: String) = flow {
        if (command.isBlank()) {
            emit(SearchSecurities(user.id))
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
            emitAll(
                it.executeCommand(
                    user = user,
                    path = path.drop(1),
                    command = command
                )
            )
            return@flow
        }
        emit(ErrorScreen(user.id, UNKNOWN_PATH))
    }

    private suspend fun command(user: User, command: String) = flow {
        navigationCommands[command]?.let {

        }

        emit(customCommand(user, command))
    }

    private suspend fun customCommand(user: User, command: String): BotScreen {
        return when (val result = getFullSecurityUseCase(command)) {
            GetFullSecurityUseCase.GetSecurityResult.SecurityNotFound -> SecurityNotFound(user.id, command)
            is GetFullSecurityUseCase.GetSecurityResult.Success -> SecuritySearchResult(
                id = user.id, messageId = null, ticker = result.fullSecurity.security.share.ticker,
                state = SecuritySearchResult.State.SearchResult(
                    result = result.fullSecurity,
                    followed = isUserSubscribed(user.id, command)
                )
            )
        }
    }
}