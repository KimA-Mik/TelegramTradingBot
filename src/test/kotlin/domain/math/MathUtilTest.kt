package domain.math

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MathUtilTest {

    @Test
    fun calculateEmas() {
        val values = doubleArrayOf(15.0, 11.0, 10.0)
        val res = MathUtil.calculateEmas(values)

        val expected = doubleArrayOf(15.0, 13.0, 11.5)
        assertArrayEquals(expected, res, 0.001)
    }

    @Test
    fun calculateEmasEmpty() {
        val values = doubleArrayOf()
        val res = MathUtil.calculateEmas(values)

        val expected = doubleArrayOf()
        assertArrayEquals(expected, res, 0.001)
    }

    @Test
    fun calculateEmasSingle() {
        val values = doubleArrayOf(123.0)
        val res = MathUtil.calculateEmas(values)

        val expected = doubleArrayOf(123.0)
        assertArrayEquals(expected, res, 0.001)
    }

    @Test
    fun calculateEma() {
        val values = doubleArrayOf(15.0, 11.0, 10.0)
        val res = MathUtil.calculateEma(values)

        val expected = 11.5
        assertEquals(expected, res, 0.001)
    }
}