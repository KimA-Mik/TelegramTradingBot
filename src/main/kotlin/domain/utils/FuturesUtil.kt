package domain.utils

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
            4 -> parseShortFutureCode(ticker)
            else -> wrongDay
        }
    }

    private fun parseShortFutureCode(ticker: String): DayOfYear {
        val code = ticker[2]
        val month = codeToMonths[code] ?: return wrongDay


        return DayOfYear(21, month)
    }
}