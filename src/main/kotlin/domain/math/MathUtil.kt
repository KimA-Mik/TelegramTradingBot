package domain.math

object MathUtil {
    const val RSI_LOW = 30.0
    const val RSI_HIGH = 70.0

    const val S_RSI_LOW = 20.0
    const val S_RSI_HIGH = 80.0

    const val RSI_BARS_COUNT = 14

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

    fun calculateRmaSeries(values: DoubleArray, period: Int): DoubleArray {
        val res = DoubleArray(values.size)

        val alpha = 1.0 / period
        res[0] = values.average()

        for (i in 1 until res.size) {
            res[i] = alpha * values[i] + (1.0 - alpha) * res[i - 1]
        }

        return res
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

        val rs = upsRma / downsRma
        val rsi = 100.0 - (100.0 / (1.0 + rs))
        return rsi
    }

    fun calculateRsiSeries(prices: DoubleArray, period: Int = 14): DoubleArray {
        val ups = DoubleArray(prices.size - 1)
        val downs = DoubleArray(prices.size - 1)
        val res = DoubleArray(prices.size - 1)

        for (i in 1 until prices.size) {
            val diff = prices[i] - prices[i - 1]
            if (diff >= 0.0) {
                ups[i - 1] = diff
            } else {
                downs[i - 1] = -diff
            }
        }

        val upsRma = calculateRmaSeries(ups, period)
        val downsRma = calculateRmaSeries(downs, period)

        for (i in res.indices) {
            val rs = upsRma[i] / downsRma[i]
            res[i] = 100.0 - (100.0 / (1.0 + rs))
        }
        return res
    }

    fun calculateStochasticRsi(rsiSeries: DoubleArray, period: Int = 14): Double {
        if (period > rsiSeries.size) {
            return 0.0
        }

        var highest = Double.NEGATIVE_INFINITY
        var lowest = Double.POSITIVE_INFINITY

        for (i in rsiSeries.lastIndex downTo rsiSeries.lastIndex - period) {
            if (rsiSeries[i] > highest) highest = rsiSeries[i]
            if (rsiSeries[i] < lowest) lowest = rsiSeries[i]
        }
        val rsi = rsiSeries.last()
        if (highest == lowest) {
            return 0.0
        }

        return (rsi - lowest) / (highest - lowest)
    }
}