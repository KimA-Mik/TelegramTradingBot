package domain.math

object MathUtil {
    fun calculateEmas(values: DoubleArray): DoubleArray {
        val count = values.size
        val res = DoubleArray(values.size)
        if (count == 0) {
            return res
        }
        res[0] = values[0]

        val weight = 2.0 / (count + 1.0)
        for (i in 1 until count) {
            res[i] = values[i] * weight + res[i - 1] * (1 - weight)
        }

        return res
    }

    fun calculateEma(values: DoubleArray): Double {
        val count = values.size
        var res = 0.0
        if (count == 0) {
            return res
        }
        res = values.first()

        val weight = 2.0 / (count + 1.0)
        for (i in 1 until count) {
            res = values[i] * weight + res * (1 - weight)
        }

        return res
    }

    fun calculateRsi(prices: DoubleArray): Double {
        val size = prices.size
        if (size < 2) {
            return 0.0
        }

        val ups = DoubleArray(size - 1)
        val downs = DoubleArray(size - 1)

        for (i in 1 until size) {
            val diff = prices[i] - prices[i - 1]
            if (diff > 0.0) {
                ups[i - 1] = diff
            } else {
                downs[i - 1] = -diff
            }
        }

        val upsEma = calculateEma(ups)
        val downsEma = calculateEma(downs)

        val rs = upsEma / downsEma
        val rsi = 100.0 - (100.0 / (1.0 + rs))
        return rsi
    }
}