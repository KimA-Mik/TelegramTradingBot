package ru.kima.cacheserver.implementation.data

import data.remoteservice.*
import data.remoteservice.mappers.toTInstrumentStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.Share
import ru.kima.cacheserver.api.schema.model.requests.GetCandlesRequest
import ru.kima.cacheserver.api.schema.model.requests.GetLastPricesRequest
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest
import ru.kima.cacheserver.implementation.core.CachedValue
import ru.kima.cacheserver.implementation.core.RateLimiter
import ru.kima.cacheserver.implementation.core.ServerExceptions
import ru.kima.cacheserver.implementation.data.mappers.toFuture
import ru.kima.cacheserver.implementation.data.mappers.toHistoricalCandle
import ru.kima.cacheserver.implementation.data.mappers.toLastPrice
import ru.kima.cacheserver.implementation.data.mappers.toShare
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toOrderBook
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toTCandleInterval
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toTCandleSource
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toTPriceType
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval
import ru.ttech.piapi.core.connector.ConnectorConfiguration
import ru.ttech.piapi.core.connector.ServiceStubFactory
import ru.ttech.piapi.core.connector.streaming.StreamManagerFactory
import ru.ttech.piapi.core.connector.streaming.StreamServiceStubFactory
import ru.ttech.piapi.core.impl.marketdata.MarketDataStreamManager
import ru.ttech.piapi.core.impl.marketdata.subscription.CandleSubscriptionSpec
import ru.ttech.piapi.core.impl.marketdata.subscription.Instrument
import java.util.*
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

//Make dynamic
private const val TINKOFF_UNARY_REQUEST_LIMIT = 50
private val TINKOFF_RATE_WINDOW = 1.minutes

typealias TCandleSource = ru.tinkoff.piapi.contract.v1.GetCandlesRequest.CandleSource

class TinkoffDataSource(token: String) {
    private val marketDataService: MarketDataService
    private val instrumentsService: InstrumentsService
    private val marketDataStreamManager: MarketDataStreamManager
    private val rateLimiter = RateLimiter(
        limit = TINKOFF_UNARY_REQUEST_LIMIT,
        rateWindow = TINKOFF_RATE_WINDOW
    )

    //Use concurrent data structures
    private val candleSubscriptions = mutableMapOf<String, MutableSet<String>>()
    private val candleSubscriptionMutex = Mutex()

    init {
        val properties = Properties()
        properties.setProperty("token", token)
        val configuration = ConnectorConfiguration
            .loadFromProperties(properties)
        val unaryServiceFactory = ServiceStubFactory.create(configuration)

        marketDataService = unaryServiceFactory.newAsyncService(MarketDataServiceGrpc::newStub)
        instrumentsService = unaryServiceFactory.newAsyncService(InstrumentsServiceGrpc::newStub)

        val streamServiceFactory = StreamServiceStubFactory.create(unaryServiceFactory)
        val streamManagerFactory = StreamManagerFactory.create(streamServiceFactory)
        val executorService = Executors.newCachedThreadPool()
        val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        marketDataStreamManager =
            streamManagerFactory.newMarketDataStreamManager(executorService, scheduledExecutorService)
        marketDataStreamManager.start()
    }

    private val sharesCache =
        mutableMapOf<Pair<InstrumentStatus, InstrumentExchangeType>, CachedValue<List<Share>>>().withDefault {
            CachedValue(cacheLifetime = 12.hours) {
                rateLimiter.rateLimitedResult {
                    return@rateLimitedResult instrumentsService
                        .shares(it.first, it.second)
                        .await().instrumentsList.map { tinkoffShare -> tinkoffShare.toShare() }
                }
            }
        }

    suspend fun shares(
        instrumentStatus: InstrumentStatus,
        instrumentExchangeType: InstrumentExchangeType
    ) = accessSharesCache(instrumentStatus to instrumentExchangeType).getValue()

    private fun accessSharesCache(key: Pair<InstrumentStatus, InstrumentExchangeType>): CachedValue<List<Share>> {
        if (!sharesCache.contains(key)) {
            sharesCache[key] = sharesCache.getValue(key)
        }

        return sharesCache.getValue(key)
    }

    private val futuresCache =
        mutableMapOf<Pair<InstrumentStatus, InstrumentExchangeType>, CachedValue<List<Future>>>().withDefault {
            CachedValue(cacheLifetime = 12.hours) {
                rateLimiter.rateLimitedResult {
                    return@rateLimitedResult instrumentsService
                        .futures(it.first, it.second)
                        .await().instrumentsList.map { tinkoffFuture -> tinkoffFuture.toFuture() }
                }
            }
        }

    private fun accessFuturesCache(key: Pair<InstrumentStatus, InstrumentExchangeType>): CachedValue<List<Future>> {
        if (!futuresCache.contains(key)) {
            futuresCache[key] = futuresCache.getValue(key)
        }

        return futuresCache.getValue(key)
    }

    suspend fun futures(
        instrumentStatus: InstrumentStatus,
        instrumentExchangeType: InstrumentExchangeType
    ) = accessFuturesCache(instrumentStatus to instrumentExchangeType).getValue()

    @OptIn(ExperimentalTime::class)
    suspend fun getCandles(request: GetCandlesRequest) = rateLimiter.rateLimitedResult {
        runCatching {
            marketDataService.getCandles(
                uid = request.uid,
                from = request.from,
                to = request.to,
                interval = request.interval.toTCandleInterval(),
                candleSource = request.candleSource.toTCandleSource(),
            )
                .await()
                .candlesList
                .map { it.toHistoricalCandle() }
        }
    }

    suspend fun getOrderBook(request: GetOrderBookRequest) = rateLimiter.rateLimitedResult {
        runCatching {
            marketDataService.getOrderBook(
                uid = request.uid,
                depth = request.depth
            )
                .await()
                .toOrderBook()
        }
    }

    suspend fun findSecurity(ticker: String): Result<Security> = runCatching {
        val cleanedTicker = ticker.trim()
        for (instrumentStatus in InstrumentStatus.entries) {
            for (instrumentExchangeType in InstrumentExchangeType.entries) {
                val pair = instrumentStatus to instrumentExchangeType
                accessSharesCache(pair)
                    .getValue().getOrNull()
                    ?.find { it.ticker.equals(cleanedTicker, ignoreCase = true) }
                    ?.let { return@runCatching it }

                accessFuturesCache(pair)
                    .getValue().getOrNull()
                    ?.find { it.ticker.equals(cleanedTicker, ignoreCase = true) }
                    ?.let { return@runCatching it }
            }
        }
        throw ServerExceptions.SecurityNotFoundException(ticker)
    }

    suspend fun getLastPrices(request: GetLastPricesRequest) = runCatching {
        marketDataService.getLastPrices(
            instrumentIds = request.uids,
            instrumentStatus = request.instrumentStatus.toTInstrumentStatus(),
            lastPriceType = request.lastPriceType.toTPriceType()
        )
            .await()
            .lastPricesList
            .map { it.toLastPrice() }
    }


    private val candleSubscriptionSpec = CandleSubscriptionSpec(TCandleSource.CANDLE_SOURCE_UNSPECIFIED, false)
    suspend fun subscribeToCandles(apiKey: String, ids: List<String>) {
        candleSubscriptionMutex.withLock {
            if (!candleSubscriptions.contains(apiKey)) return@withLock

            candleSubscriptions.getValue(apiKey)

        }

        marketDataStreamManager.subscribeCandles(
            setOf(
                Instrument("", SubscriptionInterval.SUBSCRIPTION_INTERVAL_MONTH)
            ), candleSubscriptionSpec
        ) { candle ->

        }
    }
}
