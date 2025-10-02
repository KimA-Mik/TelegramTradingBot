package ru.kima.cacheserver.implementation.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.kima.cacheserver.api.api.*
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentExchangeType
import ru.kima.cacheserver.api.schema.instrumentsService.InstrumentStatus
import ru.kima.cacheserver.api.schema.model.requests.GetCandlesRequest
import ru.kima.cacheserver.api.schema.model.requests.GetLastPricesRequest
import ru.kima.cacheserver.api.schema.model.requests.GetOrderBookRequest
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest
import ru.kima.cacheserver.api.util.enumValueOfOrNull
import ru.kima.cacheserver.implementation.core.ServerExceptions
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

        get("/$FIND_SECURITY") {
            val ticker = call.queryParameters[ApiResources.FindSecurity::ticker.name]
            if (ticker == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            tinkoffDataSource.findSecurity(ticker)
                .onSuccess { call.respond(it) }
                .onFailure {
                    when (it) {
                        is ServerExceptions.SecurityNotFoundException -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            call.respond(HttpStatusCode.InternalServerError)
                            log.error(it.message)
                        }
                    }
                }
        }

        get("/$LAST_PRICES") {
            val request = runCatching { call.receive<GetLastPricesRequest>() }
                .getOrElse {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

            tinkoffDataSource.getLastPrices(request)
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