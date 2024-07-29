package domain.tinkoff.repository

import Resource
import domain.tinkoff.model.*
import kotlinx.datetime.Instant

interface TinkoffRepository {
    fun getSecurity(secId: String): Resource<TinkoffShare>
    fun getSecurityFutures(security: TinkoffShare): Resource<List<TinkoffFuture>>
    suspend fun getSharesPrice(shares: List<TinkoffShare>): Resource<List<TinkoffPrice>>
    suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>>
    fun findSecurity(ticker: String): SecurityType
    suspend fun getShareCandles(
        uid: String,
        from: Instant,
        to: Instant,
        interval: TinkoffCandleInterval
    ): Resource<List<TinkoffCandle>>

    suspend fun getDailyCandles(uid: String): Resource<List<TinkoffCandle>>
    suspend fun getHourlyCandles(uid: String): Resource<List<TinkoffCandle>>
    suspend fun getOrderBook(uid: String): Resource<TinkoffOrderBook>
}