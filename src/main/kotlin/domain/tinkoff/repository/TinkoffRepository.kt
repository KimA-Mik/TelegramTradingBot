package domain.tinkoff.repository

import Resource
import domain.tinkoff.model.SecurityType
import domain.tinkoff.model.TinkoffCandle
import domain.tinkoff.model.TinkoffCandleInterval
import domain.tinkoff.model.TinkoffFuture
import domain.tinkoff.model.TinkoffOrderBook
import domain.tinkoff.model.TinkoffPrice
import domain.tinkoff.model.TinkoffShare
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface TinkoffRepository {
    fun getSecurity(secId: String): Resource<TinkoffShare>
    fun getSecurityFutures(security: TinkoffShare): Resource<List<TinkoffFuture>>
    suspend fun getSharesPrice(shares: List<TinkoffShare>): Resource<List<TinkoffPrice>>
    suspend fun getFuturesPrices(futures: List<TinkoffFuture>): Resource<List<TinkoffPrice>>
    fun findSecurity(ticker: String): SecurityType

    @OptIn(ExperimentalTime::class)
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