package domain.tinkoff.repository

import Resource
import domain.tinkoff.model.SecurityType
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare

interface TinkoffRepository {
    fun getSecurity(secId: String): Resource<TinkoffShare>
    fun getSecurityFutures(security: TinkoffShare): Resource<List<TinkoffFuture>>
    suspend fun getSharesPrice(securities: List<TinkoffShare>): Resource<List<TinkoffPrice>>
    suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>>
    fun findSecurity(ticker: String): SecurityType
}