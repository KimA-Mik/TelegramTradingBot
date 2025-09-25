package ru.kima.cacheserver.implementation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingRequest
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.kima.cacheserver.api.api.HISTORIC_CANDLES
import ru.kima.cacheserver.api.api.ORDER_BOOK
import ru.kima.cacheserver.api.api.TRADABLE_FUTURES
import ru.kima.cacheserver.api.api.TRADABLE_SHARES
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.model.requests.GetCandlesRequest
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest
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
                .onFailure { defaultOnFailure(call, it) }
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
                .onFailure { defaultOnFailure(call, it) }
        }

        get("/$HISTORIC_CANDLES") {
            val request = runCatching { call.receive<GetCandlesRequest>() }
                .getOrElse {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

            tinkoffDataSource.getCandles(request)
                .onSuccess { call.respond(it) }
                .onFailure { defaultOnFailure(call, it) }
        }

        get(path = "/$ORDER_BOOK") {
            val request = runCatching { call.receive<GetOrderBookRequest>() }
                .getOrElse {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

            tinkoffDataSource.getOrderBook(request)
                .onSuccess { call.respond(it) }
                .onFailure { defaultOnFailure(call, it) }
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

private suspend fun Application.defaultOnFailure(call: RoutingCall, throwable: Throwable) {
    call.respond(HttpStatusCode.InternalServerError)
    log.error(throwable.message)
}