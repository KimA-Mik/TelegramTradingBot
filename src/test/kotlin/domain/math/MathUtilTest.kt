package domain.math

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class MathUtilTest {

    @Test
    fun calculateEma() {
        val values = doubleArrayOf(15.0, 11.0, 10.0)
        val res = MathUtil.calculateEma(values)

        val expected = doubleArrayOf(15.0, 13.0, 11.5)
        assertArrayEquals(expected, res, 0.001)
    }

    @Test
    fun calculateEmaEmpty() {
        val values = doubleArrayOf()
        val res = MathUtil.calculateEma(values)

        val expected = doubleArrayOf()
        assertArrayEquals(expected, res, 0.001)
    }

    @Test
    fun calculateEmaSingle() {
        val values = doubleArrayOf(123.0)
        val res = MathUtil.calculateEma(values)

        val expected = doubleArrayOf(123.0)
        assertArrayEquals(expected, res, 0.001)
    }
}