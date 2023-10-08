package services

import Resource
import api.moex.data.history.HistoryEntry
import api.moex.data.security.SecurityInfo


interface RequestService {
    suspend fun getLastPrice(securityId: String): Double

    suspend fun getMarketData(securityId: String): Resource<SecurityInfo>

    suspend fun getPriceHistory(securityId: String): Resource<List<HistoryEntry>>
}