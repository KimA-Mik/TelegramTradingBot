package domain.tinkoff.usecase

import domain.util.levenshtein
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.kima.cacheserver.api.api.CacheServerApi
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.requests.FindSecurityResponse
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

class FindSecurityUseCase(private val api: CacheServerApi) {
    suspend operator fun invoke(ticker: String): Result {
        val normalizedTicker = ticker.trim().uppercase()
        return when (val res = api.findSecurity(normalizedTicker)) {
            is FindSecurityResponse.Share -> handleSecurity(res.share)
            is FindSecurityResponse.Future -> handleSecurity(res.future)
            else -> findSuggestions(normalizedTicker)
        }
    }


    private suspend fun handleSecurity(security: Security): Result {
        val orderBook = api.getOrderBook(GetOrderBookRequest(security.uid))
        return Result.Success(security, orderBook.getOrNull()?.lastPrice)
    }

    private suspend fun findSuggestions(ticker: String): Result = coroutineScope {
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
            .map { it to levenshtein(ticker, it) }
            .sortedBy { it.second }
            .take(5)
            .map { it.first }
            .toList()

        if (suggestions.isEmpty()) return@coroutineScope Result.NotFound
        return@coroutineScope Result.Suggestions(suggestions)
    }

    sealed interface Result {
        data class Success(val security: Security, val price: Double?) : Result
        data class Suggestions(val suggestions: List<String>) : Result
        data object NotFound : Result
    }
}