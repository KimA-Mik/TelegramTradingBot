package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffOrderBook
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun GetOrderBookResponse.toTinkoffOrderBook(): TinkoffOrderBook {
    return TinkoffOrderBook(
        uid = instrumentUid,
        depth = depth,
        bids = bidsList.map { it.toTinkoffOrder() },
        asks = asksList.map { it.toTinkoffOrder() },
        lastPrice = lastPrice.toDouble(),
        closePrice = closePrice.toDouble(),
        lastPriceTs = Instant.fromEpochSeconds(lastPriceTs.seconds, lastPriceTs.nanos),
        closePriceTs = Instant.fromEpochSeconds(closePriceTs.seconds, closePriceTs.nanos),
        orderBookTs = Instant.fromEpochSeconds(orderbookTs.seconds, orderbookTs.nanos),
    )
}