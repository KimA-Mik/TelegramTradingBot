package domain.tinkoff.util

import domain.tinkoff.model.TinkoffFuture
import kotlin.math.abs

object TinkoffFutureComparator : Comparator<TinkoffFuture> {
    override fun compare(o1: TinkoffFuture, o2: TinkoffFuture): Int {
        val years = try {
            val year1 = o1.ticker.last().code
            val year2 = o2.ticker.last().code
            year1 - year2
        } catch (e: NoSuchElementException) {
            0
        }
        if (abs(years) == 9) return -years
        if (years != 0) return years

        val result = try {
            val month1 = o1.ticker.last { it.isLetter() }.code
            val month2 = o2.ticker.last { it.isLetter() }.code
            month1 - month2
        } catch (e: NoSuchElementException) {
            0
        }
        return result
    }
}