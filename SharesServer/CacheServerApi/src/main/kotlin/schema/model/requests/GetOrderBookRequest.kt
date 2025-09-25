package ru.kima.cacheserver.api.schema.model.requests

import kotlinx.serialization.Serializable


const val ORDER_BOOK_DEPTH = 20

/**
 * Запрос стакана.
 */
@Serializable
data class GetOrderBookRequest(
    /**
     *Идентификатор инструмента. Принимает значение figi или instrument_uid
     */
    val uid: String,
    /**
     * Глубина стакана.
     */
    val depth: Int = ORDER_BOOK_DEPTH
)