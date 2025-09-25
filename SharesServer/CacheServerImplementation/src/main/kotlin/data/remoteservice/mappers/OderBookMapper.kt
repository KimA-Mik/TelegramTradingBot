package ru.kima.cacheserver.implementation.data.remoteservice.mappers

import ru.kima.cacheserver.api.schema.model.Order
import ru.kima.cacheserver.api.schema.model.OrderBook
import ru.kima.cacheserver.implementation.data.util.toInstant
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse
import kotlin.time.ExperimentalTime

typealias TinkoffOrder = ru.tinkoff.piapi.contract.v1.Order

@OptIn(ExperimentalTime::class)
fun GetOrderBookResponse.toOrderBook() = OrderBook(
    uid = instrumentUid,
    depth = depth,
    bids = bidsList.map { it.toOrder() },
    asks = asksList.map { it.toOrder() },
    lastPrice = lastPrice.toDouble(),
    closePrice = closePrice.toDouble(),
    limitUp = limitUp.toDouble(),
    limitDown = limitDown.toDouble(),
    lastPriceTs = lastPriceTs.toInstant(),
    closePriceTs = closePriceTs.toInstant(),
    orderBookTs = orderbookTs.toInstant(),
)

fun TinkoffOrder.toOrder() = Order(
    price = price.toDouble(),
    quantity = quantity
)