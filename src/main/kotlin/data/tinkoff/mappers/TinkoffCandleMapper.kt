package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffCandle
import ru.tinkoff.piapi.contract.v1.HistoricCandle
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun HistoricCandle.toTinkoffCandle(): TinkoffCandle {
    return TinkoffCandle(
        open = open.toDouble(),
        high = high.toDouble(),
        low = low.toDouble(),
        close = close.toDouble(),
        volume = volume,
        time = Instant.fromEpochSeconds(time.seconds, time.nanos),
        isComplete = isComplete
    )
}