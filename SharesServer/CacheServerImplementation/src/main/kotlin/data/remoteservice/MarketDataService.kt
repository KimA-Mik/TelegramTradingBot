package data.remoteservice

import kotlinx.coroutines.future.asDeferred
import ru.kima.cacheserver.api.schema.model.requests.ORDER_BOOK_DEPTH
import ru.kima.cacheserver.implementation.data.util.toTimestamp
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.connector.AsyncStubWrapper
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

typealias MarketDataService = AsyncStubWrapper<MarketDataServiceGrpc.MarketDataServiceStub>


/**
 * Цены последних сделок по инструментам.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getlastprices">Reference</a>
 */
fun MarketDataService.getLastPrices(
    instrumentIds: List<String>,
    instrumentStatus: InstrumentStatus,
    lastPriceType: LastPriceType,
) = callAsyncMethod { stub, observer ->
    val builder = GetLastPricesRequest.newBuilder()
    instrumentIds.forEach { builder.addInstrumentId(it) }
    builder.setInstrumentStatus(instrumentStatus)
    builder.setLastPriceType(lastPriceType)

    stub.getLastPrices(builder.build(), observer)
}.asDeferred()

/**
 * Исторические свечи по инструменту.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getcandles">Reference></a>
 */
@OptIn(ExperimentalTime::class)
fun MarketDataService.getCandles(
    uid: String,
    from: Instant,
    to: Instant,
    interval: CandleInterval
) = callAsyncMethod { stub, observer ->
    val builder = GetCandlesRequest.newBuilder()
        .setInstrumentId(uid)
        .setFrom(from.toTimestamp())
        .setTo(to.toTimestamp())
        .setInterval(interval)

    stub.getCandles(builder.build(), observer)
}.asDeferred()

/**
 * Стакан по инструменту.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getorderbook">Reference</a>
 */
fun MarketDataService.getOrderBook(
    uid: String,
    depth: Int = ORDER_BOOK_DEPTH
): CompletableFuture<GetOrderBookResponse> = callAsyncMethod { stub, observer ->
    val builder = GetOrderBookRequest.newBuilder()
        .setInstrumentId(uid)
        .setDepth(depth)

    stub.getOrderBook(builder.build(), observer)
}
