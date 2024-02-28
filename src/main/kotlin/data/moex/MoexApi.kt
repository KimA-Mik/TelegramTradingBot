package data.moex

import Resource
import data.moex.data.emitter.securities.EmitterSecurity
import data.moex.data.emitter.securities.EmitterSecurityResponse
import data.moex.data.history.HistoryEntry
import data.moex.data.history.HistoryResponse
import data.moex.data.security.SecurityInfo
import data.moex.data.security.SecurityResponse
import data.moex.data.securityMetadata.SecurityMetadata
import data.moex.data.securityMetadata.SecurityMetadataResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MoexApi(private val client: HttpClient) {
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

            Resource.Success(history[1].history!!)
        } catch (e: Exception) {
            println(e.message)
            Resource.Error("При поиске истории цен для $securityId произошла чудовищная ошибка")
        }
    }

    //example sec: https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/SBER.json?iss.meta=off&iss.json=extended
    //example futures: https://iss.moex.com/iss/engines/futures/markets/forts/boards/RFUD/securities/SRZ3.json?iss.meta=off&iss.json=extended
    suspend fun getMarketData(
        securityId: String,
        engine: String = "stock",
        market: String = "shares",
        board: String = "TQBR"
    ): Resource<SecurityInfo> {
        return try {
            val requestUrl =
                "$URL/engines/$engine/markets/$market/boards/$board/securities/$securityId.json?$DEF_AGRS"
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
            println(e.message)
            Resource.Error("При поиске последней цены для $securityId произошла чудовищная ошибка")
        }
    }

    //example: https://iss.moex.com/iss/securities/GAZP.jsonp?iss.meta=off&iss.json=extended&lang=ru&shortname=1
    suspend fun getSecurityMetadata(securityId: String): Resource<SecurityMetadata> {
        return try {
            val requestUrl =
                "$URL/securities/$securityId.json?$DEF_AGRS"
            val response = client.get(requestUrl)
            val result: Array<SecurityMetadataResponse> = response.body()

            val data = result[1]
            val description = data.description
            val boards = data.boards
            if (description?.isNotEmpty() == true && boards?.isNotEmpty() == true) {
                Resource.Success(
                    SecurityMetadata(
                        description, boards
                    )
                )
            } else {
                Resource.Error("Методата для $securityId не найдена")
            }
        } catch (e: Exception) {
            println(e.message)
            Resource.Error("При поиске метадаты для $securityId произошла чудовищная ошибка")
        }
    }

    //example: https://iss.moex.com/iss/emitters/1243/securities.jsonp?iss.meta=off&iss.json=extended&lang=ru
    suspend fun getEmitterSecurities(emitterId: Int): Resource<List<EmitterSecurity>> {
        return try {
            val requestUrl =
                "$URL/emitters/$emitterId/securities.json?$DEF_AGRS&lang=ru"
            val response = client.get(requestUrl)
            val result: Array<EmitterSecurityResponse> = response.body()
            val data = result[1]
            val securities = data.securities

            if (securities?.isNotEmpty() == true) {
                Resource.Success(securities)
            } else {
                Resource.Error("Не удалост получить иформацию о бумагах эмитента $emitterId")
            }
        } catch (e: Exception) {
            println(e.message)
            Resource.Error("При получении информации о бумагах эмитента $emitterId произошла чудовищная ошибка")
        }
    }
}
