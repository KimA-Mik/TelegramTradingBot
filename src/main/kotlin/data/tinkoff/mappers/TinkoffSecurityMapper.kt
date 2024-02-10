package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffSecurity
import ru.tinkoff.piapi.contract.v1.Share

fun Share.toTinkoffSecurity(): TinkoffSecurity {
    return TinkoffSecurity(
        uid = uid,
        ticker = ticker,
        name = name,
        lot = lot
    )
}