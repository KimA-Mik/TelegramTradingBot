package api.investing

import api.extension.await
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class InvsetingApi(private val client: OkHttpClient) {
    private val URL = "https://api.investing.com/api"

    suspend fun performSearch(query: String): Response {
        val requestUrl = "$URL/search/v2/search?q=$query"
        val request = Request.Builder()
            .url(requestUrl)
            .header("Domain-Id", "ru")
            .get()
            .build()

        return client.newCall(request).await()
    }

    suspend fun getRecentPriceHistory(id: Int): Response {
        val requestUrl = "$URL/financialdata/$id/historical/chart/?interval=PT1M&pointscount=60"
        val request = Request.Builder()
            .url(requestUrl)
            .header("Domain-Id", "ru")
            .get()
            .build()

        return client.newCall(request).await()
    }
}