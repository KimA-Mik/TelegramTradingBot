package presentation.telegram.security.search.textmodel

import domain.config.LocalConfigDataSource
import domain.tinkoff.usecase.FindSecurityUseCase
import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen
import presentation.telegram.security.search.screen.SearchSecurityScreen
import presentation.telegram.security.search.screen.TickerSearchResultScreen

class SearchSecurityTextModel(
    private val findSecurity: FindSecurityUseCase,
    private val localConfig: LocalConfigDataSource,
) : TextModel {
    override val node = NavigationRoot.SecuritySearch
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            val hssDefaultSecurities = localConfig.getLocalConfig().securities.isNotEmpty()
            emit(SearchSecurityScreen(user.id, showFillSuggestions = hssDefaultSecurities))
            return@flow
        }

        when (command) {
            Commands.UseDefaultSecurities.text -> {

            }

            else -> {
                val searchResult = runCatching { findSecurity(user.id, command) }
                    .onFailure { logger.error(it.message) }
                    .getOrDefault(FindSecurityUseCase.Result.NotFound)
                emit(TickerSearchResultScreen(user.id, searchResult = searchResult))
            }
        }
    }

    enum class Commands(val text: String) {
        UseDefaultSecurities("Подписаться на бумаги по умолчанию")
    }
}