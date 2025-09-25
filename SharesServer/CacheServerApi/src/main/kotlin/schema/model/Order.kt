package ru.kima.cacheserver.api.schema.model

import kotlinx.serialization.Serializable

/**Массив предложений/спроса.*/
@Serializable
data class Order(
    /**Цена за 1 инструмент. Чтобы получить стоимость лота, нужно умножить на лотность инструмента*/
    val price: Double,
    /**Количество в лотах.*/
    val quantity: Long
)
