package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffPrice
import ru.tinkoff.piapi.contract.v1.LastPrice
import java.time.Instant
import java.time.ZoneId

private val zoneMoscow = ZoneId.of("Europe/Moscow")

fun LastPrice.toTinkoffPrice(): TinkoffPrice {
    val dateTime = Instant
        .ofEpochSecond(time.seconds, time.nanos.toLong())
        .atZone(zoneMoscow)
        .toLocalDateTime()

    return TinkoffPrice(
        uid = instrumentUid,
        dateTime = dateTime,
        price = price.toDouble()
    )
}