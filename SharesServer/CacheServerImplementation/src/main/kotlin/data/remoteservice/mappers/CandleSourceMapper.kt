package ru.kima.cacheserver.implementation.data.remoteservice.mappers

import ru.kima.cacheserver.api.schema.model.requests.GetCandlesRequest


private typealias TCandleSource = ru.tinkoff.piapi.contract.v1.GetCandlesRequest.CandleSource

fun GetCandlesRequest.CandleSource.toTCandleSource(): TCandleSource = when (this) {
    GetCandlesRequest.CandleSource.UNSPECIFIED -> TCandleSource.CANDLE_SOURCE_UNSPECIFIED
    GetCandlesRequest.CandleSource.UNRECOGNIZED -> TCandleSource.UNRECOGNIZED
    GetCandlesRequest.CandleSource.EXCHANGE -> TCandleSource.CANDLE_SOURCE_EXCHANGE
    GetCandlesRequest.CandleSource.INCLUDE_WEEKEND -> TCandleSource.CANDLE_SOURCE_INCLUDE_WEEKEND
}