package ru.kima.cacheserver.implementation.data

import data.remoteservice.InstrumentsService
import data.remoteservice.MarketDataService
import data.remoteservice.futures
import data.remoteservice.getCandles
import data.remoteservice.shares
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Share
import ru.kima.cacheserver.api.schema.model.requests.GetCandlesRequest
import ru.kima.cacheserver.implementation.core.CachedValue
import ru.kima.cacheserver.implementation.core.RateLimiter
import ru.kima.cacheserver.implementation.data.mappers.toFuture
import ru.kima.cacheserver.implementation.data.mappers.toHistoricalCandle
import ru.kima.cacheserver.implementation.data.mappers.toShare
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toTCandleInterval
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.ttech.piapi.core.connector.ConnectorConfiguration
import ru.ttech.piapi.core.connector.ServiceStubFactory
import java.util.Properties
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

//Make dynamic
private const val TINKOFF_UNARY_REQUEST_LIMIT = 50
private val TINKOFF_RATE_WINDOW = 1.minutes

class TinkoffDataSource(token: String) {
    private val marketDataService: MarketDataService
    private val instrumentsService: InstrumentsService
    private val rateLimiter = RateLimiter(
        limit = TINKOFF_UNARY_REQUEST_LIMIT,
        rateWindow = TINKOFF_RATE_WINDOW
    )

    init {
        val properties = Properties()
        properties.setProperty("token", token)
        val configuration = ConnectorConfiguration
            .loadFromProperties(properties)
        val unaryServiceFactory = ServiceStubFactory.create(configuration)

        marketDataService = unaryServiceFactory.newAsyncService(MarketDataServiceGrpc::newStub)
        instrumentsService = unaryServiceFactory.newAsyncService(InstrumentsServiceGrpc::newStub)
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
    ) = sharesCache.getValue(instrumentStatus to instrumentExchangeType).getValue()

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

    suspend fun futures(
        instrumentStatus: InstrumentStatus,
        instrumentExchangeType: InstrumentExchangeType
    ) = futuresCache.getValue(instrumentStatus to instrumentExchangeType).getValue()

    @OptIn(ExperimentalTime::class)
    suspend fun getCandles(request: GetCandlesRequest) = rateLimiter.rateLimitedResult {
        runCatching {
            marketDataService.getCandles(
                uid = request.uid,
                from = request.from,
                to = request.to,
                interval = request.interval.toTCandleInterval()
            )
                .await()
                .candlesList
                .map { it.toHistoricalCandle() }
        }
    }
}