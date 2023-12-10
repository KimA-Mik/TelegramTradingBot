package data.moex.repository

import Resource
import data.moex.MoexApi
import data.moex.data.emitter.securities.EmitterSecurity
import data.moex.data.history.HistoryEntry
import data.moex.data.security.SecurityInfo
import data.moex.data.securityMetadata.SecurityMetadata
import domain.futures.repository.RequestService


class RequestServiceImpl(private val moexApi: MoexApi) : RequestService {
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
