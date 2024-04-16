package domain.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime

object FuturesUtil {
    private val wrongDay = DayOfYear(-1, -1)
    val codeToMonths =
        mapOf(
            'F' to 1,
            'G' to 2,
            'H' to 3,
            'J' to 4,
            'K' to 5,
            'M' to 6,
            'N' to 7,
            'Q' to 8,
            'U' to 9,
            'V' to 10,
            'X' to 11,
            'Z' to 12
        )

    fun getFutureExpireDay(ticker: String): DayOfYear {
        return when (ticker.length) {
            4 -> parseShortFutureExpirationDay(ticker)
            else -> wrongDay
        }
    }

    fun getFutureExpirationYear(
        ticker: String,
        currentYear: Int =
            Clock.System.now()
                .toLocalDateTime(DateUtil.timezoneMoscow).year
    ): Int {
        return when (ticker.length) {
            4 -> parseShortFutureExpirationYear(ticker, currentYear)
            else -> -1
        }
    }

    private fun parseShortFutureExpirationDay(
        ticker: String,
    ): DayOfYear {
        val code = ticker[2]
        val month = codeToMonths[code] ?: return wrongDay

        return DayOfYear(21, month)
    }

    private fun parseShortFutureExpirationYear(
        ticker: String,
        currentYear: Int
    ): Int {
        val expirationYear = ticker.last().digitToIntOrNull() ?: return -1
        val current = currentYear % 10
        if (expirationYear == current) return currentYear
        if (expirationYear > current) return currentYear - current + expirationYear
        return currentYear - current + 10 + expirationYear
    }
}