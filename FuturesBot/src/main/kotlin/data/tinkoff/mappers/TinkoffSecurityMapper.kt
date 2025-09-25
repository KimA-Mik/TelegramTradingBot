package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffShare
import ru.tinkoff.piapi.contract.v1.Share

fun Share.toTinkoffSecurity(): TinkoffShare {
    return TinkoffShare(
        uid = uid,
        ticker = ticker,
        name = name,
        lot = lot
    )
}