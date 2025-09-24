package ru.kima.cacheserver.api.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Share
import ru.kima.cacheserver.api.schema.model.requests.InstrumentsRequest

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
            url(apiUrl)
        }
    }

    suspend fun tradableShares(request: InstrumentsRequest): Result<List<Share>> =
        handleGetResponse(client.get(ApiResources.TradableShares(request)))

    suspend fun tradableFutures(request: InstrumentsRequest): Result<List<Future>> =
        handleGetResponse(client.get(ApiResources.TradableFutures(request)))

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