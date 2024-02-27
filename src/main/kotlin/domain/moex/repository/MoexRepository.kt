package domain.moex.repository

import Resource
import data.moex.data.emitter.securities.EmitterSecurity
import data.moex.data.history.HistoryEntry
import data.moex.data.security.SecurityInfo
import data.moex.data.securityMetadata.SecurityMetadata
import domain.moex.securities.model.Security


interface MoexRepository {
    suspend fun getLastPrice(securityId: String): Double

    suspend fun getMarketData(
        securityId: String,
        engine: String = "stock",
        market: String = "shares",
        board: String = "TQBR"
    ): Resource<SecurityInfo>

    suspend fun getPriceHistory(securityId: String): Resource<List<HistoryEntry>>

    suspend fun getSecurityMetadata(securityId: String): Resource<SecurityMetadata>

    suspend fun getEmitterSecurities(emitterId: Int): Resource<List<EmitterSecurity>>

    suspend fun getSecurity(secId: String): Resource<Security>
}