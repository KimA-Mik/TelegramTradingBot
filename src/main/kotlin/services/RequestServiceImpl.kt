package services

import Resource
import api.moex.MoexApi
import api.moex.data.history.HistoryEntry
import api.moex.data.security.SecurityInfo


class RequestServiceImpl : RequestService {
    private val moexApi = MoexApi()

    override suspend fun getLastPrice(securityId: String): Double {
        return 0.0
    }

    override suspend fun getMarketData(securityId: String): Resource<SecurityInfo> =
        moexApi.getMarketData(securityId)


    override suspend fun getPriceHistory(securityId: String): Resource<List<HistoryEntry>> =
        moexApi.getPriceHistory(securityId)

}