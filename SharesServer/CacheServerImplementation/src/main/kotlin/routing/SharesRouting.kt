package ru.kima.cacheserver.implementation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.implementation.data.TinkoffDataSource

fun Application.sharesRouting(
    tinkoffDataSource: TinkoffDataSource
) {
    routing {
        get("/") {
            call.respondText("Hello world!!!")
        }

        get("/tradableShares") {
            tinkoffDataSource.shares(
                InstrumentStatus.INSTRUMENT_STATUS_BASE,
                InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
            )
                .onSuccess { call.respond(it) }
                .onFailure { call.respondText("", status = HttpStatusCode.NotFound) }
        }
        get("/tradableFutures") {
            tinkoffDataSource.futures(
                InstrumentStatus.INSTRUMENT_STATUS_BASE,
                InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
            )
                .onSuccess { call.respond(it) }
                .onFailure { call.respondText("", status = HttpStatusCode.NotFound) }
        }
    }
}