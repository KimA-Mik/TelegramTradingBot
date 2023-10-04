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
        if (idCache.containsKey(ticker))
            return idCache[ticker]!!

        val response = invsetingApi.performSearch(ticker)
        if (response.code == 200 && response.body != null) {
            val body = response.body!!.string()
//                println(body)
            val obj = format.decodeFromString<SearchResponse>(body)

            for (quote in obj.quotes) {
                if (quote.symbol == ticker) {
                    idCache[ticker] = quote.id
                    return quote.id
                }
            }
        }
        return 0

    }

    suspend fun getLastPrice(ticker: String): Double {
        val id = getInvestingTickerId(ticker)

        if (id < 1)
            return 0.0

        val response = invsetingApi.getRecentPriceHistory(id)

        if (response.code != 200)
            return 0.0

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
        val result = format.decodeFromJsonElement<Double>(last.jsonArray[4])
        return result
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