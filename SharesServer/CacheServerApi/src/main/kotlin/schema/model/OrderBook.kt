package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Информация о стакане.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getorderbookresponse">Reference</a>
 * */
@Serializable
data class OrderBook @OptIn(ExperimentalTime::class) constructor(
    /** UID инструмента.*/
    val uid: String,
    /**Глубина стакана*/
    val depth: Int,
    /**Массив предложений.*/
    val bids: List<Order>,
    /**Массив спроса.*/
    val asks: List<Order>,
    /**Цена последней сделки за 1 инструмент*/
    val lastPrice: Double,
    /**Цена закрытия за 1 инструмент*/
    val closePrice: Double,
    /**Верхний лимит цены за 1 инструмент*/
    val limitUp: Double,
    /**Нижний лимит цены за 1 инструмент*/
    val limitDown: Double,
    /**Время получения цены последней сделки.*/
    val lastPriceTs: Instant,
    /**Время получения цены закрытия.*/
    val closePriceTs: Instant,
    /**Время формирования стакана на бирже.*/
    val orderBookTs: Instant
)
