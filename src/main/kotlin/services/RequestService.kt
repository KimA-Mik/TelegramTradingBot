package services

import api.investing.InvsetingApi
import api.investing.SearchResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import java.util.*


class RequestService private constructor() {
    private val format = Json { ignoreUnknownKeys = true }
    private val invsetingApi = InvsetingApi(client)
    private val idCache = mutableMapOf<String, Int>()

    suspend fun getInvestingTickerId(ticker: String): Int {
        val result = invsetingApi.performSearch(ticker)
        if (result.code == 200 && result.body != null) {
            val body = result.body!!.string()
            println(body)
            val obj = format.decodeFromString<SearchResponse>(body)

            for (quote in obj.quotes) {
                if (quote.symbol == ticker) {
                    return quote.id
                }
            }
        }
        return 0
    }

    suspend fun getLastPrice(ticker: String):Double {
        val id = if (idCache.containsKey(ticker)){
            idCache[ticker]!!
        } else {
            val id = getInvestingTickerId(ticker)
            idCache[ticker] = id
            id
        }
        if (id < 1)
            return 0.0

        val result = invsetingApi.getRecentPriceHistory(id)
        if (result.code != 200)
            return 0.0

        val body = result.body!!.string()
        val root = format.parseToJsonElement(body)
        val data = root.jsonObject["data"]!!

        for (line in data.jsonArray) {
            println(line)
        }

        val last = data.jsonArray.last()
        val date = Date(format.decodeFromJsonElement<Long>(last.jsonArray[0]))
        val price = format.decodeFromJsonElement<Double>(last.jsonArray[1])
        return price
    }

    companion object {
        private val client = OkHttpClient()
        private var instance: RequestService? = null
//        private val JSON = "application/json".toMediaType()
//        private val HTML = "text/html; charset=utf-8".toMediaType()

        fun get(): RequestService {
            if (instance == null)
                instance = RequestService()
            return instance!!
        }
    }
}