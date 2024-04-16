package domain.utils

import kotlinx.datetime.*

object FuturesUtil {
    private val wrongDay = DayOfYear(-1, -1)
    private val codeToMonths =
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

    fun getFutureAnnualPercent(
        ticker: String,
        difference: Double,
        current: Instant = Clock.System.now()
    ): Double {
        val days = getDaysUntilExpiration(ticker, current)
        if (days < 1) return 0.0
        return difference * 365.0 / days
    }

    fun getDaysUntilExpiration(
        ticker: String,
        current: Instant = Clock.System.now()
    ): Int {
        return when (ticker.length) {
            4 -> daysUntilShortCodeExpiration(ticker, current)
            else -> -1
        }
    }

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

    private fun daysUntilShortCodeExpiration(
        ticker: String,
        from: Instant = Clock.System.now()
    ): Int {
        val dayOfExpiration = parseShortFutureExpirationDay(ticker)
        if (dayOfExpiration == wrongDay) return -1

        val yearOfExpiration = parseShortFutureExpirationYear(
            ticker = ticker,
            currentYear = from.toLocalDateTime(DateUtil.timezoneMoscow).year
        )
        if (yearOfExpiration < 0) return -1

        val dateOfExpiration = LocalDate(
            year = yearOfExpiration,
            monthNumber = dayOfExpiration.month,
            dayOfMonth = dayOfExpiration.day
        ).atStartOfDayIn(DateUtil.timezoneMoscow)

        return from.daysUntil(dateOfExpiration, DateUtil.timezoneMoscow)
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