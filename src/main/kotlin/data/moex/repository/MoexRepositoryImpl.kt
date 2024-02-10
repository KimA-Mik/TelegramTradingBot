package data.moex.repository

import Resource
import data.moex.MoexApi
import data.moex.data.emitter.securities.EmitterSecuritiesTypes
import data.moex.data.emitter.securities.EmitterSecurity
import data.moex.data.history.HistoryEntry
import data.moex.data.security.SecurityInfo
import data.moex.data.securityMetadata.SecurityMetadata
import data.moex.data.securityMetadata.SecurityMetadataTypes
import domain.moex.repository.MoexRepository
import domain.moex.securities.model.Futures
import domain.moex.securities.model.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


class MoexRepositoryImpl(private val moexApi: MoexApi) : MoexRepository {
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

    override suspend fun getSecurity(secId: String): Resource<Security> = coroutineScope {
        val metadata = moexApi.getSecurityMetadata(secId)
        if (metadata is Resource.Error) {
            return@coroutineScope Resource.Error("${metadata.message}")
        }

        val emitterId =
            metadata.data?.description?.find { it.name == SecurityMetadataTypes.EMITTER_ID.type }?.value?.toInt()
                ?: return@coroutineScope Resource.Error("Эмитент для $secId не найден")
//                ?: return Resource.Error(getFuturesStrFromMetadata(metadata.data))

        val emitterSecuritiesResource = moexApi.getEmitterSecurities(emitterId)
        if (emitterSecuritiesResource is Resource.Error) {
            return@coroutineScope Resource.Error("${emitterSecuritiesResource.message}")
        }

        val emitterSecurities = emitterSecuritiesResource.data
        val commonShare = emitterSecurities?.find { it.type == EmitterSecuritiesTypes.COMMON_SHARE.type }
            ?: return@coroutineScope Resource.Error("Не удалось найти акцию $secId")

        val commonShareJob = async(Dispatchers.IO) {
            moexApi.getMarketData(
                commonShare.secId,
                commonShare.engine,
                commonShare.market,
                commonShare.primaryBoardId
            )
        }

        val futures = emitterSecurities
            .filter { it.type == EmitterSecuritiesTypes.FUTURES.type }
            .map { security ->
                async(Dispatchers.IO) {
                    moexApi.getMarketData(
                        security.secId,
                        security.engine,
                        security.market,
                        security.primaryBoardId
                    )
                }
            }

        val commonShareResource = commonShareJob.await()

        if (commonShareResource is Resource.Error) {
            return@coroutineScope Resource.Error(commonShareResource.message!!)
        }

        val commonSecurity = commonShareResource.data!!


        val resFutures = futures
            .awaitAll()
            .asSequence()
            .filterIsInstance<Resource.Success<SecurityInfo>>()
            .map { result ->
                result.data!!
            }.map { securityInfo ->
                Futures(
                    secId = securityInfo.security.secId,
                    shortName = securityInfo.security.shortName,
                    name = securityInfo.security.secName,
                    price = securityInfo.marketData.last,
                    lotSize = securityInfo.security.lotSize,
                    time = securityInfo.marketData.time
                )
            }
            .toList()

        return@coroutineScope Resource.Success(
            Security(
                secId = commonSecurity.security.secId,
                shortName = commonSecurity.security.shortName,
                name = commonSecurity.security.secName,
                price = commonSecurity.marketData.last,
                emitter = emitterId,
                futures = resFutures,
                time = commonSecurity.marketData.time
            )
        )
    }
}
