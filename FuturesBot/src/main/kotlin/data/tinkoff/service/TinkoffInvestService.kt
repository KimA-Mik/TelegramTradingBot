package data.tinkoff.service

import Resource
import data.tinkoff.util.TApi
import data.tinkoff.util.service.getCandles
import data.tinkoff.util.service.getLastPrices
import data.tinkoff.util.service.getOrderBook
import data.tinkoff.util.service.instrumentsService.InstrumentExchangeType
import data.tinkoff.util.service.instrumentsService.InstrumentStatus
import data.tinkoff.util.service.instrumentsService.futures
import data.tinkoff.util.service.instrumentsService.shares
import domain.math.MathUtil
import domain.utils.TimeUtil
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class TinkoffInvestService(
//    private val api: InvestApi,
//    private val marketDataService: MarketDataService,
    private val tApi: TApi
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var tradableShares: Map<String, Share> =
        emptyMap()  //MutableStateFlow(emptyList<Share>())
    private var tradableFutures: Map<String, Future> =
        emptyMap() //= MutableStateFlow(emptyList<Future>())
    private var sharesToFutures: Map<String, List<Future>> = emptyMap()

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
        return sharesToFutures[ticker] ?: emptyList()
    }

    suspend fun getUidsLastPrices(uids: List<String>): List<LastPrice> {
        val response = tApi.marketDataService.getLastPrices(uids).await()
        return response.lastPricesList

//        return withContext(Dispatchers.IO) {
//            api.marketDataService.getLastPricesSync(uids)
//        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getShareClosePriceHistory(
        uid: String,
        from: Instant,
        to: Instant,
        interval: CandleInterval
    ): List<HistoricCandle> {
        return try {
            tApi.marketDataService.getCandles(uid, from, to, interval)
                .await()
                .candlesList
//                api.marketDataService.getCandlesSync(
//                    uid,
//                    from.toJavaInstant(),
//                    to.toJavaInstant(),
//                    interval
//                )
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getOrderBook(
        uid: String,
        depth: Int = MathUtil.ORDER_BOOK_DEPTH
    ): Resource<GetOrderBookResponse> {
        return try {
            val response = tApi.marketDataService.getOrderBook(uid, depth).await()
//            val response = api
//                .marketDataService
//                .getOrderBook(uid, depth)
//                .asDeferred()
//                .await()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private suspend fun updateShares() = coroutineScope {
        var errorsCount = 0L
        while (isActive) {
            val shares = try {
                tApi.instrumentsService.shares(
                    InstrumentStatus.INSTRUMENT_STATUS_BASE,
                    InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
                )
                    .await()
                    .instrumentsList

//                api.instrumentsService
//                    .tradableShares
//                    .asDeferred().await()
            } catch (e: Exception) {
                logger.error("Unable to update shares because of: ${e.localizedMessage} (${e.cause})")
                emptyList()
            }

            if (shares.isNotEmpty()) {
                tradableShares = shares.associateBy { it.ticker }
                errorsCount = 0L
                delay(24L * TimeUtil.HOUR_MILLIS)
            } else {
                errorsCount++
                delay(errorsCount * TimeUtil.MINUTE_MILLIS)
            }
        }
    }

    private suspend fun updateFutures() = coroutineScope {
        var errorsCount = 0L
        while (isActive) {
            val futures = try {
                tApi.instrumentsService.futures(
                    InstrumentStatus.INSTRUMENT_STATUS_BASE,
                    InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
                )
                    .await()
                    .instrumentsList
//                api.instrumentsService
//                    .tradableFutures
//                    .asDeferred().await()
            } catch (e: Exception) {
                logger.error("Unable to update futures because of: ${e.message} (${e.cause})\n${e.stackTraceToString()}")

                emptyList()
            }

            if (futures.isNotEmpty()) {
                tradableFutures = futures.associateBy { it.ticker }
                sharesToFutures = futures.groupBy { it.basicAsset }
                errorsCount = 0L
                delay(24L * TimeUtil.HOUR_MILLIS)
            } else {
                errorsCount++
                delay(errorsCount * TimeUtil.MINUTE_MILLIS)
            }
        }
    }
}