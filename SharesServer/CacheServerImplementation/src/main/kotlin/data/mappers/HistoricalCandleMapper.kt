package ru.kima.cacheserver.implementation.data.mappers

import ru.kima.cacheserver.api.schema.model.HistoricCandle
import ru.kima.cacheserver.implementation.data.remoteservice.mappers.toDouble
import ru.kima.cacheserver.implementation.data.util.toInstant
import kotlin.time.ExperimentalTime

typealias TinkoffHistoricCandle = ru.tinkoff.piapi.contract.v1.HistoricCandle

@OptIn(ExperimentalTime::class)
fun TinkoffHistoricCandle.toHistoricalCandle() = HistoricCandle(
    open = open.toDouble(),
    high = high.toDouble(),
    low = low.toDouble(),
    close = close.toDouble(),
    volume = volume,
    time = time.toInstant(),
    isComplete = isComplete
)
