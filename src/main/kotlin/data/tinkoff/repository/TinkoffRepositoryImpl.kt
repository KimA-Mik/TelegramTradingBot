package data.tinkoff.repository

import Resource
import data.tinkoff.mappers.toTinkoffFuture
import data.tinkoff.mappers.toTinkoffPrice
import data.tinkoff.mappers.toTinkoffSecurity
import domain.tinkoff.model.SecurityType
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tinkoff.piapi.contract.v1.Future
import ru.tinkoff.piapi.contract.v1.LastPrice
import ru.tinkoff.piapi.contract.v1.Share
import ru.tinkoff.piapi.core.InvestApi

class TinkoffRepositoryImpl(private val api: InvestApi) : TinkoffRepository {
    private val tradableShares: List<Share> = api.instrumentsService.tradableSharesSync
    private val tradableFutures: List<Future> = api.instrumentsService.tradableFuturesSync


    override fun getSecurity(secId: String): Resource<TinkoffShare> {
        val res = tradableShares.find { it.ticker.equals(secId.trim(), ignoreCase = true) }

        return if (res == null) {
            Resource.Error("Security not found")
        } else {
            Resource.Success(res.toTinkoffSecurity())
        }
    }

    override fun getSecurityFutures(security: TinkoffShare): Resource<List<TinkoffFuture>> {
        val futures = tradableFutures
            .filter { it.basicAsset == security.ticker }
            .map { it.toTinkoffFuture() }

        return if (futures.isEmpty()) {
            Resource.Error("Futures not found")
        } else {
            Resource.Success(futures)
        }
    }

    override suspend fun getSecuritiesPrice(securities: List<TinkoffShare>): Resource<List<TinkoffPrice>> {
        val uids = securities.map { it.uid }
        val tinkoffPrices = getUidsLastPrices(uids)

        return if (tinkoffPrices.isEmpty()) {
            Resource.Error("")
        } else {
            Resource.Success(tinkoffPrices)
        }
    }

    override suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>> {
        val uids = futures.map { it.uid }
        val tinkoffPrices = getUidsLastPrices(uids)

        return if (tinkoffPrices.isEmpty()) {
            Resource.Error("")
        } else {
            Resource.Success(tinkoffPrices)
        }
    }

    override fun findSecurity(ticker: String): SecurityType {
        TODO("Not yet implemented")
    }

    private suspend fun getUidsLastPrices(uids: List<String>): List<TinkoffPrice> {
        val prices = withContext(Dispatchers.IO) {
            api.marketDataService.getLastPricesSync(uids)
        }
        return prices.map(LastPrice::toTinkoffPrice)
    }
}