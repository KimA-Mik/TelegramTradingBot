package presentation.telegram.textModels

import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffSecurity
import domain.tinkoff.repository.TinkoffRepository
import domain.user.model.User
import presentation.telegram.BotScreen
import presentation.telegram.model.SecuritySearchResultData
import presentation.telegram.textModels.common.TextModel
import presentation.telegram.textModels.common.UNKNOWN_PATH

class SearchSecuritiesTextModel(
    private val tinkoffRepository: TinkoffRepository
) : TextModel {
    private val textModels = mapOf<String, TextModel>()

    private val navigationCommands = mapOf<String, TextModel>()

    override suspend fun executeCommand(user: User, path: List<String>, command: String): BotScreen {
        if (command.isBlank()) {
            return BotScreen.SearchSecurities(user.id)
        }

        return if (path.isEmpty()) {
            command(user, command)
        } else {
            passExecution(user, path, command)
        }
    }

    private suspend fun passExecution(
        user: User,
        path: List<String>,
        command: String
    ): BotScreen {
        val nextScreen = path.first()

        return if (textModels.containsKey(nextScreen)) {
            return textModels[nextScreen]!!.executeCommand(
                user = user,
                path = path.drop(1),
                command = command
            )
        } else {
            BotScreen.Error(user.id, UNKNOWN_PATH)
        }
    }

    private suspend fun command(user: User, command: String): BotScreen {
        navigationCommands[command]?.let {
            return navigateCommand(user, command, it)
        }

        return customCommand(user, command)
    }

    //TODO: Factor out search
    private suspend fun customCommand(user: User, command: String): BotScreen {
        val shareResource = tinkoffRepository.getSecurity(command)
        if (shareResource.data == null) {
            return BotScreen.SecurityNotFound(user.id, command)
        }

        val share = shareResource.data
        val futuresResource = tinkoffRepository.getSecurityFutures(share)
        val futures = futuresResource.data ?: emptyList()

        val sharesPrices = tinkoffRepository.getSharesPrice(listOf(share))
        val sharePrice = sharesPrices.data?.getOrNull(0) ?: TinkoffPrice()

        val futuresPrices = tinkoffRepository.getFuturesPrices(futures).data ?: emptyList()

        val data = SecuritySearchResultData(
            security = TinkoffSecurity(
                share = share,
                futures = futures
            ),
            sharePrice = sharePrice,
            futuresPrices = futuresPrices
        )
        return BotScreen.SecuritySearchResult(user.id, data)
    }

    private suspend fun navigateCommand(user: User, destination: String, model: TextModel): BotScreen {
//        navigateUser(user, destination)
//        return model.executeCommand(user, emptyList(), String())
        return BotScreen.Error(user.id, UNKNOWN_PATH)
    }
}