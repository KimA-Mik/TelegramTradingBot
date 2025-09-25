package ru.kima.cacheserver.implementation.data.remoteservice.mappers

import ru.tinkoff.piapi.contract.v1.Quotation
import kotlin.math.abs
import kotlin.math.sign

fun Quotation.toDouble(): Double {
    val high = units.toDouble()
    val low = if (nano != 0) {
        "0.${abs(nano)}".toDouble() * nano.sign
    } else {
        0.0
    }

    return high + low
}