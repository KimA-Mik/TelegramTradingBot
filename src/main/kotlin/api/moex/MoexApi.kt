package api.moex


import Resource
import api.moex.data.history.HistoryEntry
import api.moex.data.history.HistoryResponse
import api.moex.data.security.SecurityInfo
import api.moex.data.security.SecurityResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MoexApi : KoinComponent {
    private val client: HttpClient by inject()
    private val URL = "https://iss.moex.com/iss"
    private val DEF_AGRS = "iss.meta=off&iss.json=extended"
    suspend fun getPriceHistory(securityId: String): Resource<List<HistoryEntry>> {
        return try {
            val requestUrl =
                "$URL/history/engines/stock/markets/shares/boards/TQBR/securities/$securityId.json?$DEF_AGRS"
            val result = client.get(requestUrl)
            val history: Array<HistoryResponse> = result.body()
            if (history[1].history == null ||
                history[1].history?.isEmpty() == true
            ) {
                Resource.Error<List<HistoryEntry>>("$securityId не найден")
            }

            Resource.Success(history[1].history)
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error("При поиске истории цен для $securityId произошла чудовищная ошибка")
        }
    }

    suspend fun getMarketData(securityId: String): Resource<SecurityInfo> {
        return try {
            val requestUrl =
                "$URL/engines/stock/markets/shares/boards/TQBR/securities/$securityId.json?$DEF_AGRS"
            val response = client.get(requestUrl)
            val result: Array<SecurityResponse> = response.body()

            val data = result[1]
            val security = data.securities?.getOrNull(0)
            val marketData = data.marketData?.getOrNull(0)
            if (security != null && marketData != null) {
                Resource.Success(
                    SecurityInfo(
                        security, marketData
                    )
                )
            } else {
                Resource.Error("Последняя цена для $securityId не найдена")
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error("При поиске последней цены для $securityId произошла чудовищная ошибка")
        }
    }
}
