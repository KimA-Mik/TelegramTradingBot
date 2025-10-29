package domain.tinkoff.usecase

import domain.user.repository.UserRepository
import domain.util.levenshtein
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

class FindSecurityUseCase(
    private val api: CacheServerApi,
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Long, ticker: String): Result {
        val normalizedTicker = ticker.trim()
        return when (val res = api.findSecurity(normalizedTicker)) {
            is FindSecurityResponse.Share -> handleSecurity(userId, res.share)
            is FindSecurityResponse.Future -> handleSecurity(userId, res.future)
            else -> findSuggestions(normalizedTicker)
        }
    }

    private suspend fun handleSecurity(userId: Long, security: Security): Result {
        val orderBook = api.getOrderBook(GetOrderBookRequest(security.uid))
        val fullUser = repository.findFullUserById(userId)
        return Result.Success(
            security = security, price = orderBook.getOrNull()?.lastPrice,
            subscribed = fullUser?.securities?.any { it.uid == security.uid } == true
        )
    }

    private suspend fun findSuggestions(ticker: String): Result = coroutineScope {
        val t = ticker.uppercase().lowercase()
        val sharesDeferred = async { api.tradableShares(InstrumentsRequest.default) }
        val futuresDeferred = async { api.tradableFutures(InstrumentsRequest.default) }
        val shares = sharesDeferred.await().getOrNull()?.map { it.ticker }
        val futures = futuresDeferred.await().getOrNull()?.map { it.ticker }

        val tickers = buildList {
            shares?.let { addAll(it) }
            futures?.let { addAll(it) }
        }

        val suggestions = tickers
            .asSequence()
            .map { it to levenshtein(t, it.uppercase().lowercase()) }
            .sortedBy { it.second }
            .take(5)
            .map { it.first }
            .toList()

        if (suggestions.isEmpty()) return@coroutineScope Result.NotFound
        return@coroutineScope Result.Suggestions(suggestions)
    }

    sealed interface Result {
        data class Success(val security: Security, val price: Double?, val subscribed: Boolean) : Result
        data class Suggestions(val suggestions: List<String>) : Result
        data object NotFound : Result
    }
}