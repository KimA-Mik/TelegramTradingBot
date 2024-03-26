package data.tinkoff.repository

import Resource
import data.tinkoff.mappers.toTinkoffFuture
import data.tinkoff.mappers.toTinkoffPrice
import data.tinkoff.mappers.toTinkoffSecurity
import data.tinkoff.service.TinkoffInvestService
import domain.tinkoff.model.SecurityType
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import domain.tinkoff.repository.TinkoffRepository
import ru.tinkoff.piapi.contract.v1.Future
import ru.tinkoff.piapi.contract.v1.LastPrice

class TinkoffRepositoryImpl(private val service: TinkoffInvestService) : TinkoffRepository {

    override fun getSecurity(secId: String): Resource<TinkoffShare> {
        val result = service.findShare(ticker = secId) ?: return Resource.Error("Акция не найдена")
        return Resource.Success(result.toTinkoffSecurity())
    }

    override fun getSecurityFutures(security: TinkoffShare): Resource<List<TinkoffFuture>> {
        val futures = service.getFuturesForShare(security.ticker)

        return if (futures.isEmpty()) {
            Resource.Error("Futures not found")
        } else {
            Resource.Success(futures.map(Future::toTinkoffFuture))
        }
    }

    override suspend fun getSharesPrice(shares: List<TinkoffShare>): Resource<List<TinkoffPrice>> {
        if (shares.isEmpty()) {
            Resource.Success(emptyList<TinkoffPrice>())
        }
        val uids = shares.map { it.uid }

        return getTinkoffPriceForUids(uids)
    }

    override suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>> {
        if (futures.isEmpty()) {
            Resource.Success(emptyList<TinkoffPrice>())
        }
        val uids = futures.map { it.uid }

        return getTinkoffPriceForUids(uids)
    }

    override fun findSecurity(ticker: String): SecurityType {
        service.findShare(ticker)?.let {
            return SecurityType.SHARE
        }

        service.findFuture(ticker)?.let {
            return SecurityType.FUTURE
        }

        return SecurityType.NONE
    }

    private suspend fun getTinkoffPriceForUids(uids: List<String>): Resource<List<TinkoffPrice>> {
        return try {
            val tinkoffPrices = service
                .getUidsLastPrices(uids)
                .map(LastPrice::toTinkoffPrice)

            if (tinkoffPrices.isEmpty()) {
                Resource.Error("")
            } else {
                Resource.Success(tinkoffPrices)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}