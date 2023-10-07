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
import okhttp3.Protocol
import java.util.*


class RequestService private constructor() {
    private val format = Json { ignoreUnknownKeys = true }
    private val invsetingApi = InvsetingApi(client)
    private val idCache = mutableMapOf<String, InvestingSecurity>()

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
            println(e.message)
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
            println(e.message)
            return Resource.Error("Не удалось получить цену для $ticker")
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

    companion object {
        private val client = OkHttpClient.Builder().protocols(
            listOf(Protocol.HTTP_1_1)
        ).build()
        private var instance: RequestService? = null
//        private val JSON = "application/json".toMediaType()
//        private val HTML = "text/html; charset=utf-8".toMediaType()

        fun get(): RequestService {
            if (instance == null) {
                instance = RequestService()
            }
            return instance!!
        }
    }
}