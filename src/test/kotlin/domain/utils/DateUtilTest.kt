package domain.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class DateUtilTest {

    @Test
    fun msUntilStartOfDaySimple() {
        val stat = LocalDateTime(
            2024, 4, 15,
            0, 0, 0
        ).toInstant(DateUtil.timezoneMoscow)
        val end = LocalDateTime(
            2024, 4, 22,
            0, 0, 0
        ).toInstant(DateUtil.timezoneMoscow)

        val expectedDiff = stat.until(end, DateTimeUnit.MILLISECOND)

        assertEquals(
            expectedDiff,
            DateUtil.msUntilStartOfDay(stat, DayOfWeek.MONDAY)
        )
    }

    @Test
    fun msUntilStartOfDayMoreComplicated() {
        val stat = LocalDateTime(
            2024, 4, 15,
            0, 0, 0
        ).toInstant(DateUtil.timezoneMoscow)
        val end = LocalDateTime(
            2024, 4, 22,
            12, 0, 0
        ).toInstant(DateUtil.timezoneMoscow)

        val expectedDiff = stat.until(
            end.minus(12, DateTimeUnit.HOUR),
            DateTimeUnit.MILLISECOND
        )

        assertEquals(
            expectedDiff,
            DateUtil.msUntilStartOfDay(stat, DayOfWeek.MONDAY)
        )
    }

    @Test
    fun isHoursInRange() {
        assertTrue(DateUtil.isHoursInRange(12, 0, 9, 30, 17, 59))
        assertTrue(DateUtil.isHoursInRange(9, 31, 9, 30, 17, 59))
        assertTrue(DateUtil.isHoursInRange(9, 30, 9, 30, 17, 59))
        assertTrue(DateUtil.isHoursInRange(17, 59, 9, 30, 17, 59))
        assertFalse(DateUtil.isHoursInRange(17, 59, 9, 30, 17, 58))
        assertFalse(DateUtil.isHoursInRange(9, 29, 9, 30, 17, 58))
        assertFalse(DateUtil.isHoursInRange(0, 0, 9, 30, 17, 58))
    }

    @Test
    fun msUntilTime() {
        var stat = LocalDateTime(
            2024, 4, 15,
            1, 0
        ).toInstant(DateUtil.timezoneMoscow)
        var end = LocalDateTime(
            2024, 4, 15,
            9, 30
        ).toInstant(DateUtil.timezoneMoscow)

        var expectedDiff = stat.until(end, DateTimeUnit.MILLISECOND)

        assertEquals(
            expectedDiff,
            DateUtil.msUntilTime(stat, 9, 30)
        )

        stat = LocalDateTime(
            2024, 4, 15,
            9, 20
        ).toInstant(DateUtil.timezoneMoscow)
        end = LocalDateTime(
            2024, 4, 15,
            9, 30
        ).toInstant(DateUtil.timezoneMoscow)

        expectedDiff = stat.until(end, DateTimeUnit.MILLISECOND)

        assertEquals(
            expectedDiff,
            DateUtil.msUntilTime(stat, 9, 30)
        )

        stat = LocalDateTime(
            2024, 4, 14,
            18, 0
        ).toInstant(DateUtil.timezoneMoscow)
        end = LocalDateTime(
            2024, 4, 15,
            9, 30
        ).toInstant(DateUtil.timezoneMoscow)

        expectedDiff = stat.until(end, DateTimeUnit.MILLISECOND)

        assertEquals(
            expectedDiff,
            DateUtil.msUntilTime(stat, 9, 30)
        )
    }
}