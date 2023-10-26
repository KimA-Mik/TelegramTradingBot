package api.investing

import api.extension.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder

class InvsetingApi(private val client: OkHttpClient) {
    private val URL = "https://api.investing.com/api"

    suspend fun performSearch(query: String): Response = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val requestUrl = "$URL/search/v2/search?q=$encodedQuery"

        val request = Request.Builder()
            .url(requestUrl)
            .header("Domain-Id", "ru")
            .header("Accept-Language", "ru-RU,ru;q=0.9,en-GB;q=0.8,en;q=0.7,en-US;q=0.6")
            .get()
            .build()

        return@withContext client.newCall(request).await()
    }

    suspend fun getRecentPriceHistory(id: Int, interval: Interval = Interval.PT1M): Response = withContext(Dispatchers.IO) {
        val requestUrl = "$URL/financialdata/$id/historical/chart/?interval=$interval&pointscount=60"
        val request = Request.Builder()
            .url(requestUrl)
            .header("Domain-Id", "ru")
            .get()
            .build()

        return@withContext client.newCall(request).await()
    }

    suspend fun getFuturesPriceHistory(id: Int): Response = withContext(Dispatchers.IO) {
        val requestUrl =
            "https://ru.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id=$id&pair_id_for_news=$id&chart_type=area&pair_interval=86400&candle_count=120"
        val request = Request.Builder()
            .url(requestUrl)
            .header("Domain-Id", "ru")
            .header("Accept","application/json, text/javascript, */*; q=0.01")
            .header("Accept-Language", "ru-RU,ru;q=0.9,en-GB;q=0.8,en;q=0.7,en-US;q=0.6")
            .get()
            .build()

        return@withContext client.newCall(request).await()
    }

    enum class Interval() {
        P1D, //1 Day
        PT1M //1 Minute
    }
}