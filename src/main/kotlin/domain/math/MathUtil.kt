package domain.math

object MathUtil {
    const val RSI_LOW = 30.0
    const val RSI_HIGH = 70.0

    const val ORDER_BOOK_DEPTH = 20
    const val BOLLINGER_BARS_COUNT = 20
    const val DEFAULT_BSI_COUNT = 14

    //https://stackoverflow.com/questions/69980426/how-to-get-the-same-rsi-as-tradingview-in-java
    fun calculateRma(values: DoubleArray, period: Int): Double {
        if (values.isEmpty()) {
            return 0.0
        }

        val alpha = 1.0 / period
        val initialRma = values.average()
        return values.fold(initialRma) { prevRma, value ->
            alpha * value + (1.0 - alpha) * prevRma
        }
    }


    fun calculateRsi(prices: DoubleArray, period: Int = DEFAULT_BSI_COUNT): Double {
        if (prices.isEmpty()) {
            return 0.0
        }

        val ups = DoubleArray(prices.size - 1)
        val downs = DoubleArray(prices.size - 1)

        for (i in 1 until prices.size) {
            val diff = prices[i] - prices[i - 1]
            if (diff >= 0.0) {
                ups[i - 1] = diff
            } else {
                downs[i - 1] = -diff
            }
        }

        val upsRma = calculateRma(ups, period)
        val downsRma = calculateRma(downs, period)

        val rs = upsRma / downsRma
        val rsi = 100.0 - (100.0 / (1.0 + rs))
        return rsi
    }
}