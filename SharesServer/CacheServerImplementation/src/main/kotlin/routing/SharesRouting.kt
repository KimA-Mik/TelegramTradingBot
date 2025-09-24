package ru.kima.cacheserver.implementation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingRequest
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.kima.cacheserver.api.api.TRADABLE_FUTURES
import ru.kima.cacheserver.api.api.TRADABLE_SHARES
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest
import ru.kima.cacheserver.api.util.enumValueOfOrNull
import ru.kima.cacheserver.implementation.data.TinkoffDataSource

fun Application.sharesRouting(
    tinkoffDataSource: TinkoffDataSource
) {
    routing {
        get("/") {
            call.respondText("Hello world!!!")
        }

        get("/$TRADABLE_SHARES") {
            val request = call.request.extractInstrumentsRequest()
            if (request == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            tinkoffDataSource.shares(
                request.instrumentStatus,
                request.instrumentExchangeType
            )
                .onSuccess { call.respond(it) }
                .onFailure { call.respondText("", status = HttpStatusCode.NotFound) }
            call.request.queryParameters
        }

        get("/$TRADABLE_FUTURES") {
            val request = call.request.extractInstrumentsRequest()
            if (request == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            tinkoffDataSource.futures(
                request.instrumentStatus,
                request.instrumentExchangeType
            )
                .onSuccess { call.respond(it) }
                .onFailure { call.respondText("", status = HttpStatusCode.NotFound) }
        }
    }
}

private fun RoutingRequest.extractInstrumentsRequest(
    defaultValues: InstrumentsRequest = InstrumentsRequest.default
): InstrumentsRequest? {
    return InstrumentsRequest(
        instrumentStatus = when (val value =
            queryParameters[InstrumentsRequest::instrumentStatus.name]) {
            null -> defaultValues.instrumentStatus
            else -> enumValueOfOrNull<InstrumentStatus>(value) ?: return null
        },
        instrumentExchangeType = when (val value =
            queryParameters[InstrumentsRequest::instrumentExchangeType.name]) {
            null -> defaultValues.instrumentExchangeType
            else -> enumValueOfOrNull<InstrumentExchangeType>(value) ?: return null
        },
    )
}