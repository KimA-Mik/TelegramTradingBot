package ru.kima.telegrambot.common.util

import org.ta4j.core.Indicator
import org.ta4j.core.num.Num

fun Indicator<Num>.lastDouble(): Double {
    return getValue(barSeries.endIndex).doubleValue()
}