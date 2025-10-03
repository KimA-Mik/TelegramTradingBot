package ru.kima.cacheserver.implementation.data.remoteservice.mappers

import ru.kima.cacheserver.api.schema.marketdataService.LastPriceType

typealias TinkoffLastPriceType = ru.tinkoff.piapi.contract.v1.LastPriceType

fun TinkoffLastPriceType.toLastPriceType() = when (this) {
    TinkoffLastPriceType.LAST_PRICE_UNSPECIFIED -> LastPriceType.LAST_PRICE_UNSPECIFIED
    TinkoffLastPriceType.LAST_PRICE_EXCHANGE -> LastPriceType.LAST_PRICE_EXCHANGE
    TinkoffLastPriceType.LAST_PRICE_DEALER -> LastPriceType.LAST_PRICE_DEALER
    TinkoffLastPriceType.UNRECOGNIZED -> LastPriceType.UNRECOGNIZED
}

fun LastPriceType.toTPriceType() = when (this) {
    LastPriceType.LAST_PRICE_UNSPECIFIED -> TinkoffLastPriceType.LAST_PRICE_UNSPECIFIED
    LastPriceType.LAST_PRICE_EXCHANGE -> TinkoffLastPriceType.LAST_PRICE_EXCHANGE
    LastPriceType.LAST_PRICE_DEALER -> TinkoffLastPriceType.LAST_PRICE_DEALER
    LastPriceType.UNRECOGNIZED -> TinkoffLastPriceType.UNRECOGNIZED
}