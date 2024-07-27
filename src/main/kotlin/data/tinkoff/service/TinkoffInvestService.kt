package data.tinkoff.service

import Resource
import domain.math.MathUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.future.asDeferred
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import ru.tinkoff.piapi.contract.v1.*
import ru.tinkoff.piapi.core.InvestApi

class TinkoffInvestService(private val api: InvestApi) {
    //TODO: Use maps for faster search
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

    suspend fun getShareClosePriceHistory(
        uid: String,
        from: Instant,
        to: Instant,
        interval: CandleInterval
    ): List<HistoricCandle> {
        return withContext(Dispatchers.IO) {
            try {
                api.marketDataService.getCandlesSync(
                    uid,
                    from.toJavaInstant(),
                    to.toJavaInstant(),
                    interval
                )
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getOrderBook(uid: String, depth: Int = MathUtil.ORDER_BOOK_DEPTH): Resource<GetOrderBookResponse> {
        return try {
            val response = api
                .marketDataService
                .getOrderBook(uid, depth)
                .asDeferred()
                .await()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
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