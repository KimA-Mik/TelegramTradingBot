package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffFuture
import ru.tinkoff.piapi.contract.v1.Future
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Future.toTinkoffFuture(): TinkoffFuture {
    return TinkoffFuture(
        uid = uid,
        ticker = ticker,
        name = name,
        lot = lot,
        expirationDate = Instant.fromEpochSeconds(expirationDate.seconds)
    )
}