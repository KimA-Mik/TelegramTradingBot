package domain.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FuturesUtilTest {

    @Test
    fun getFutureExpireDay() {
        assertEquals(FuturesUtil.getFutureExpireDay("BEM4"), DayOfYear(21, 6))
        assertEquals(FuturesUtil.getFutureExpireDay("GKZ4"), DayOfYear(21, 12))
        assertEquals(FuturesUtil.getFutureExpireDay("SAK4"), DayOfYear(21, 5))
        assertEquals(FuturesUtil.getFutureExpireDay("BNU4"), DayOfYear(21, 9))
    }
}