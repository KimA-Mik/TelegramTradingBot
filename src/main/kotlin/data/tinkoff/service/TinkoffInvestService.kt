package data.tinkoff.service

import Resource
import domain.math.MathUtil
import domain.utils.TimeUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asDeferred
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.*
import ru.tinkoff.piapi.core.InvestApi

class TinkoffInvestService(private val api: InvestApi) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var tradableShares: Map<String, Share> = emptyMap()  //MutableStateFlow(emptyList<Share>())
    private var tradableFutures: Map<String, Future> = emptyMap() //= MutableStateFlow(emptyList<Future>())
    private var sharesToFurures: Map<String, List<Future>> = emptyMap()

    suspend fun launchUpdating() = coroutineScope {
        launch { updateShares() }
        launch { updateFutures() }
    }

    fun findShare(ticker: String): Share? {
        return tradableShares[ticker] // .value.find { it.ticker.equals(ticker, true) }
    }

    fun findFuture(ticker: String): Future? {
        return tradableFutures[ticker]  //.value.find { it.ticker.equals(ticker, true) }
    }

    fun getFuturesForShare(ticker: String): List<Future> {
        return sharesToFurures[ticker] ?: emptyList()
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
            val shares = try {
                api.instrumentsService
                    .tradableShares
                    .asDeferred().await()
            } catch (e: Exception) {
                logger.error("Unable to update shares because of: ${e.message}")
                emptyList()
            }

            if (shares.isNotEmpty()) {
                tradableShares = shares.associateBy { it.ticker }
                errorsCount = 0L
                delay(24L * TimeUtil.HOURS_MILLIS)
            } else {
                errorsCount++
                delay(errorsCount * TimeUtil.MINUTES_MILLIS)
            }
        }
    }

    private suspend fun updateFutures() = coroutineScope {
        var errorsCount = 0L
        while (isActive) {
            val futures = try {
                api.instrumentsService
                    .tradableFutures
                    .asDeferred().await()
            } catch (e: Exception) {
                logger.error("Unable to update futures because of: ${e.message}")
                emptyList()
            }

            if (futures.isNotEmpty()) {
                tradableFutures = futures.associateBy { it.ticker }
                sharesToFurures = futures.groupBy { it.basicAsset }
                errorsCount = 0L
                delay(24L * TimeUtil.HOURS_MILLIS)
            } else {
                errorsCount++
                delay(errorsCount * TimeUtil.MINUTES_MILLIS)
            }
        }
    }
}