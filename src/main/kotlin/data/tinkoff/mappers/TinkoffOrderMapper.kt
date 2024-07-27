package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffOrder
import ru.tinkoff.piapi.contract.v1.Order

fun Order.toTinkoffOrder(): TinkoffOrder {
    return TinkoffOrder(
        price = price.toDouble(),
        quantity = quantity
    )
}