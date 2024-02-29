package data.tinkoff.service

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tinkoff.piapi.contract.v1.Future
import ru.tinkoff.piapi.contract.v1.LastPrice
import ru.tinkoff.piapi.contract.v1.Share
import ru.tinkoff.piapi.core.InvestApi

class TinkoffInvestService(private val api: InvestApi) {
    private val tradableShares = MutableStateFlow(emptyList<Share>())
    private val tradableFutures = MutableStateFlow(emptyList<Future>())

    suspend fun launchUpdating() = coroutineScope {
        launch { updateShares() }
        launch { updateFutures() }
    }

    fun findShare(ticker: String): Share? {
        return tradableShares.value.find { it.ticker.equals(ticker, true) }
    }

    fun findFuture(ticker: String): Future? {
        return tradableFutures.value.find { it.ticker.equals(ticker, true) }
    }

    fun getFuturesForShare(ticker: String): List<Future> {
        val futures = tradableFutures.value
            .filter { it.basicAsset.equals(ticker, true) }

        return futures
    }

    suspend fun getUidsLastPrices(uids: List<String>): List<LastPrice> {
        return withContext(Dispatchers.IO) {
            api.marketDataService.getLastPricesSync(uids)
        }
    }

    private suspend fun updateShares() = coroutineScope {
        var errorsCount = 0L
        while (isActive) {
            val shares = withContext(Dispatchers.IO) {
                try {
                    api.instrumentsService.tradableSharesSync
                } catch (e: Exception) {
                    println(e)
                    return@withContext emptyList<Share>()
                }
            }

            if (shares.isNotEmpty()) {
                tradableShares.value = shares
                errorsCount = 0L
                delay(24L * HOURS)
            } else {
                errorsCount++
                delay(errorsCount * MINUTES)
            }
        }
    }

    private suspend fun updateFutures() = coroutineScope {
        var errorsCount = 0L
        while (isActive) {
            val futures = withContext(Dispatchers.IO) {
                try {
                    api.instrumentsService.tradableFuturesSync
                } catch (e: Exception) {
                    println(e)
                    return@withContext emptyList<Future>()
                }
            }

            if (futures.isNotEmpty()) {
                tradableFutures.value = futures
                errorsCount = 0L
                delay(24L * HOURS)
            } else {
                errorsCount++
                delay(errorsCount * MINUTES)
            }
        }
    }

    companion object {
        private const val MILLIS = 1000L
        private const val SECONDS = 60L * MILLIS
        private const val MINUTES = 60L * SECONDS
        private const val HOURS = 60L * MINUTES
    }
}