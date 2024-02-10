package data.tinkoff.mappers

import domain.tinkoff.model.TinkoffPrice
import ru.tinkoff.piapi.contract.v1.LastPrice
import java.time.Instant
import java.time.ZoneId
import kotlin.math.abs

private val zoneMoscow = ZoneId.of("Europe/Moscow")

fun LastPrice.toTinkoffPrice(): TinkoffPrice {
    val dateTime = Instant
        .ofEpochSecond(time.seconds, time.nanos.toLong())
        .atZone(zoneMoscow)
        .toLocalDateTime()

    val high = price.units.toDouble()
    val low = if (price.nano != 0) {
        "0.${abs(price.nano)}".toDouble()
    } else {
        0.0
    }

    val price = high + low

    return TinkoffPrice(
        uid = instrumentUid,
        dateTime = dateTime,
        price = price
    )
}