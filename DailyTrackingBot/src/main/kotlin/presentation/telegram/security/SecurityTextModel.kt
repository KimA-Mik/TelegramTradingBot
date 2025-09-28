package presentation.telegram.security

import domain.tinkoff.usecase.GetSecurityUseCase
import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import presentation.telegram.core.NavigationRoot
import presentation.telegram.core.TextModel
import presentation.telegram.core.screen.BotScreen
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest

class SecurityTextModel(
    private val getSecurity: GetSecurityUseCase,
    private val api: CacheServerApi,
) : TextModel {
    override val node = NavigationRoot.Security

    override fun executeCommand(
        user: User,
        path: List<String>,
        command: String
    ): Flow<BotScreen> = flow {
        if (command.isBlank()) {
            val security = user.ticker?.let { ticker ->
                when (val res = getSecurity(ticker)) {
                    is FindSecurityResponse.Share -> res.share
                    is FindSecurityResponse.Future -> res.future
                    else -> null
                }
            }
            val lastPrice = security?.let { api.getOrderBook(GetOrderBookRequest(it.uid)).getOrNull()?.lastPrice }
            emit(SecurityScreen(user, security, lastPrice))
        }
        TODO("Not yet implemented")
    }
}