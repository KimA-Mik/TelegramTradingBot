package ru.kima.cacheserver.implementation.data.mappers

import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.implementation.data.util.toInstant
import kotlin.time.ExperimentalTime

typealias TinkoffFuture = ru.tinkoff.piapi.contract.v1.Future

@OptIn(ExperimentalTime::class)
fun TinkoffFuture.toFuture() = Future(
    uid = uid,
    ticker = ticker,
    name = name,
    lot = lot,
    expirationDate = expirationDate.toInstant()
)
