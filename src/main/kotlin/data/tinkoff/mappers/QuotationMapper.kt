package data.tinkoff.mappers

import ru.tinkoff.piapi.contract.v1.Quotation
import kotlin.math.abs

fun Quotation.toDouble(): Double {
    val high = units.toDouble()
    val low = if (nano != 0) {
        "0.${abs(nano)}".toDouble()
    } else {
        0.0
    }

    return high + low
}