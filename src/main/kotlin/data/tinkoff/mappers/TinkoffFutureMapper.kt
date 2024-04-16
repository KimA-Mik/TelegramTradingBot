package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffFuture
import kotlinx.datetime.Instant
import ru.tinkoff.piapi.contract.v1.Future

fun Future.toTinkoffFuture(): TinkoffFuture {
    return TinkoffFuture(
        uid = uid,
        ticker = ticker,
        name = name,
        lot = lot,
        expirationDate = Instant.fromEpochSeconds(expirationDate.seconds)
    )
}