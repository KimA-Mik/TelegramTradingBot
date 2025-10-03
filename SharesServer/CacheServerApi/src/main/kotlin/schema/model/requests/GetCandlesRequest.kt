package ru.kima.cacheserver.api.schema.model.requests

import kotlinx.serialization.Serializable
import ru.kima.cacheserver.api.schema.marketdataService.CandleInterval
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class GetCandlesRequest @OptIn(ExperimentalTime::class) constructor(
    val uid: String,
    val from: Instant,
    val to: Instant,
    val interval: CandleInterval,
    val candleSource: CandleSource
) {
    enum class CandleSource {
        /** Все свечи.*/
        UNSPECIFIED,

        /** Биржевые свечи (торговые сессии).*/
        EXCHANGE,

        /** Все свечи с учетом торговли по выходным.*/
        INCLUDE_WEEKEND,
        UNRECOGNIZED
    }
}