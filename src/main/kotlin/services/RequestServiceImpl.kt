package services

import Resource
import api.moex.MoexApi
import api.moex.data.emitter.securities.EmitterSecurity
import api.moex.data.history.HistoryEntry
import api.moex.data.security.SecurityInfo
import api.moex.data.securityMetadata.SecurityMetadata


class RequestServiceImpl : RequestService {
    private val moexApi = MoexApi()

    override suspend fun getLastPrice(securityId: String): Double {
        return 0.0
    }

    override suspend fun getMarketData(
        securityId: String,
        engine: String,
        market: String,
        board: String
    ): Resource<SecurityInfo> =
        moexApi.getMarketData(securityId, engine, market, board)

    override suspend fun getPriceHistory(securityId: String): Resource<List<HistoryEntry>> =
        moexApi.getPriceHistory(securityId)


    override suspend fun getSecurityMetadata(securityId: String): Resource<SecurityMetadata> =
        moexApi.getSecurityMetadata(securityId)

    override suspend fun getEmitterSecurities(emitterId: Int): Resource<List<EmitterSecurity>> =
        moexApi.getEmitterSecurities(emitterId)
}
