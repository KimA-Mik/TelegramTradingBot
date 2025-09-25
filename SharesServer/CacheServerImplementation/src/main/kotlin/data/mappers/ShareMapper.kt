package ru.kima.cacheserver.implementation.data.mappers

import ru.kima.cacheserver.api.schema.model.Share

typealias TinkoffShare = ru.tinkoff.piapi.contract.v1.Share

fun TinkoffShare.toShare() = Share(
    uid = uid,
    ticker = ticker,
    name = name,
    lot = lot
)