package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffOrderBook
import kotlinx.datetime.Instant
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse

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