package domain.math

object MathUtil {
    const val RSI_LOW = 30.0
    const val RSI_HIGH = 70.0

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


    fun calculateRsi(prices: DoubleArray, period: Int = 14): Double {
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

        if (downsRma == 0.0) {
            return 0.0
        }

        val rs = upsRma / downsRma
        val rsi = 100.0 - (100.0 / (1.0 + rs))
        return rsi
    }

    fun calculateStochasticRsi(values: DoubleArray, period: Int = 14): Double {
        if (values.isEmpty() || values.size <= period) {
            return 0.0
        }

        var prices = values
        val rsi = DoubleArray(period)
        for (i in 0 until period) {
            rsi[rsi.size - 1 - i] = calculateRsi(prices, period)

            val temp = DoubleArray(prices.size - 1)
            for (j in temp.indices) {
                temp[j] = prices[j]
            }
            prices = temp
        }

        val highest = rsi.max()
        val lowest = rsi.min()
        val denominator = (highest - lowest)

        if (denominator == 0.0) {
            return 0.0
        }
        return (rsi.last() - lowest) / denominator
    }
}