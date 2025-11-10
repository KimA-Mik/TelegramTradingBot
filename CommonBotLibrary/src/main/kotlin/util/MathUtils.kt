package ru.kima.telegrambot.common.util

import kotlin.math.abs

fun Double.isEqual(that: Double, epsilon: Double = MathUtil.EPSILON) = abs(this - that) < epsilon