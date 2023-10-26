package services

import Resource
import api.investing.InvestingSecurity
import api.investing.InvsetingApi
import api.investing.SearchResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.logger.Logger
import java.util.*


class RequestService : KoinComponent {
    private val client: OkHttpClient by inject()
//        private val JSON = "application/json".toMediaType()
//        private val HTML = "text/html; charset=utf-8".toMediaType()

    private val format = Json { ignoreUnknownKeys = true }
    private val invsetingApi = InvsetingApi(client)
    private val idCache = mutableMapOf<String, InvestingSecurity>()
    private val logger: Logger by inject()

    suspend fun getInvestingTicker(ticker: String): Resource<InvestingSecurity> {
        try {
            val normalizedTicker = ticker.trim().uppercase(Locale.getDefault())
            if (idCache.containsKey(normalizedTicker))
                return Resource.Success(idCache[normalizedTicker]!!)
            val response = performSearch(normalizedTicker)
            for (quote in response.quotes) {
                if (quote.symbol == normalizedTicker) {
                    idCache[normalizedTicker] = quote
                    return Resource.Success(quote)
                }
            }
            return Resource.Error("Тикер $ticker не найден")
        } catch (e: Exception) {
            e.message?.let { logger.logError(it) }
            return Resource.Error("Тикер $ticker не найден")
        }

    }

    suspend fun getLastPrice(ticker: String): Resource<Price> {
        try {
            val securityResource = getInvestingTicker(ticker)
            if (securityResource is Resource.Error) {
                return Resource.Error(securityResource.message)
            }
            val security = securityResource.data!!

            val response = invsetingApi.getRecentPriceHistory(security.id)

            if (response.code != 200)
                return Resource.Error("Проблемы с сервером")

            val body = response.body!!.string()
            val root = format.parseToJsonElement(body)
            val data = root.jsonObject["data"]!!

//            for (line in data.jsonArray) {
//                println(line)
//            }

            val last = data.jsonArray.last()
            val date = Date(format.decodeFromJsonElement<Long>(last.jsonArray[0]))
//            [1] - FROM
//            [2] - LOW
//            [3] - HIGH
//            [4] - TO
            val price = format.decodeFromJsonElement<Double>(last.jsonArray[4])
            return Resource.Success(Price(date, price))
        } catch (e: Exception) {
            e.message?.let { logger.logError(it) }
            return Resource.Error("Не удалось получить цену для $ticker")
        }
    }

    suspend fun getInvestingTickerFutures(ticker: String): Resource<InvestingSecurity> {
        try {
            val normalizedTicker = ticker.trim().uppercase(Locale.getDefault())

            val response = performSearch(normalizedTicker)
            for (quote in response.quotes) {
                if (quote.description.contains("Фьючерс", true)) {
                    idCache[quote.symbol.uppercase()] = quote
                    return Resource.Success(quote)
                }
            }

            return Resource.Error("Фьючерс $ticker не найден")
        } catch (e: Exception) {
            e.message?.let { logger.logError(it) }
            return Resource.Error("Фьючерс $ticker не найден")
        }

    }

    suspend fun getFuturesLastPrice(ticker: String): Resource<Price> {
        return try {
            val futures = getInvestingTickerFutures(ticker)
            val response = futures.data?.let { invsetingApi.getFuturesPriceHistory(it.id) }

            Resource.Error("123")
        } catch (e: Exception) {
            e.message?.let { logger.logError(it) }
            Resource.Error("Цена Фьючерса $ticker не найден")
        }
    }

    private suspend fun performSearch(ticker: String): SearchResponse {
        val response = invsetingApi.performSearch(ticker)
        if (response.code == 200 && response.body != null) {
            val body = response.body!!.string()
//                println(body)
            return format.decodeFromString<SearchResponse>(body)
        }
        return SearchResponse(quotes = emptyList())
    }
}