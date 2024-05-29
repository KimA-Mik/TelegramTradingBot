package domain.math

object MathUtil {
    fun calculateEma(values: DoubleArray): DoubleArray {
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
}