package domain.tinkoff.repository

import Resource
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffSecurity

interface TinkoffRepository {
    fun getSecurity(secId: String): Resource<TinkoffSecurity>
    fun getSecurityFutures(security: TinkoffSecurity): Resource<List<TinkoffFuture>>
    suspend fun getSecuritiesPrice(securities: List<TinkoffSecurity>): Resource<List<TinkoffPrice>>
    suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>>
}