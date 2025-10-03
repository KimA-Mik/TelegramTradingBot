package ru.kima.cacheserver.implementation.data.mappers

import ru.kima.cacheserver.api.schema.model.LastPrice
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toDouble
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toLastPriceType
import ru.kima.cacheserver.implementation.data.util.toInstant
import kotlin.time.ExperimentalTime

typealias TinkoffLastPrice = ru.tinkoff.piapi.contract.v1.LastPrice

@OptIn(ExperimentalTime::class)
fun TinkoffLastPrice.toLastPrice() = LastPrice(
    uid = instrumentUid,
    price = price.toDouble(),
    time = time.toInstant(),
    lastPriceType = lastPriceType.toLastPriceType()
)
