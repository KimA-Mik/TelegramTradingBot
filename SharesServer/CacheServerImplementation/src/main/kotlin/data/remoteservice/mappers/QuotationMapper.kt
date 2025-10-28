package ru.kima.cacheserver.implementation.data.remoteservice.mappers

import ru.tinkoff.piapi.contract.v1.Quotation

fun Quotation.toDouble(): Double {
    return units + nano / 1_000_000_000.0
}