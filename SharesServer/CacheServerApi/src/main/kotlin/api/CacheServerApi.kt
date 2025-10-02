package ru.kima.cacheserver.api.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kima.cacheserver.api.schema.model.*
import ru.kima.cacheserver.api.schema.model.requests.*

class CacheServerApi(
    private val apiUrl: String,
    logLevel: LogLevel = LogLevel.INFO
) {
    private val client = HttpClient(Java) {
        install(Logging) {
            level = logLevel
        }
        install(ContentNegotiation) {
            json(Json)
        }
        install(Resources)
        defaultRequest {
            val defaultUrl = if (apiUrl.contains("://")) apiUrl
            else "http://$apiUrl"
            url(defaultUrl)
        }
    }

    suspend fun tradableShares(request: InstrumentsRequest): Result<List<Share>> =
        handleGetResponse(client.get(ApiResources.TradableShares(request)))

    suspend fun tradableFutures(request: InstrumentsRequest): Result<List<Future>> =
        handleGetResponse(client.get(ApiResources.TradableFutures(request)))

    suspend fun historicCandles(request: GetCandlesRequest): Result<List<HistoricCandle>> =
        handleGetResponse(client.get(ApiResources.HistoricCandles()) {
            contentType(ContentType.Application.Json)
            setBody(request)
        })

    suspend fun getOrderBook(request: GetOrderBookRequest): Result<OrderBook> =
        handleGetResponse(client.get(ApiResources.OrderBook()) {
            contentType(ContentType.Application.Json)
            setBody(request)
        })

    suspend fun findSecurity(ticker: String): FindSecurityResponse = try {
        val response = client.get(ApiResources.FindSecurity(ticker))
        when (response.status) {
            HttpStatusCode.OK -> when (val body = response.body<Security>()) {
                is Share -> FindSecurityResponse.Share(body)
                is Future -> FindSecurityResponse.Future(body)
            }

            HttpStatusCode.NotFound -> FindSecurityResponse.NotFound
            else -> FindSecurityResponse.UnknownError(Exception(response.status.toString()))
        }
    } catch (e: Exception) {
        FindSecurityResponse.UnknownError(e)
    }

    suspend fun lastPrices(request: GetLastPricesRequest): Result<List<LastPrice>> =
        handleGetResponse(client.get(ApiResources.LastPrices()) {
            contentType(ContentType.Application.Json)
            setBody(request)
        })

    private suspend inline fun <reified T> handleGetResponse(response: HttpResponse): Result<T> =
        try {
            if (response.status == HttpStatusCode.OK) {
                val result: T = response.body()
                Result.success(result)
            } else {
                Result.failure(IOException(response.status.toString()))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
}