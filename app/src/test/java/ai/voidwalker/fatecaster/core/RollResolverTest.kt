package ai.voidwalker.fatecaster.core

import org.junit.Assert.assertEquals
import org.junit.Test

class RollResolverTest {

    @Test
    fun `meet target is success`() {
        val result = RollResolver.resolve(
            rawRoll = 10,
            modifier = 0,
            targetNumber = 10
        )

        assertEquals(10, result.finalValue)
        assertEquals(OutcomeTier.SUCCESS, result.outcome)
        assertEquals(NaturalOverride.NONE, result.naturalOverride)
    }

    @Test
    fun `ten above target is critical success`() {
        val result = RollResolver.resolve(
            rawRoll = 19,
            modifier = 1,
            targetNumber = 10
        )

        assertEquals(20, result.finalValue)
        assertEquals(OutcomeTier.CRITICAL_SUCCESS, result.outcome)
        assertEquals(NaturalOverride.NONE, result.naturalOverride)
    }

    @Test
    fun `one below target is failure`() {
        val result = RollResolver.resolve(
            rawRoll = 10,
            modifier = -1,
            targetNumber = 10
        )

        assertEquals(9, result.finalValue)
        assertEquals(OutcomeTier.FAILURE, result.outcome)
        assertEquals(NaturalOverride.NONE, result.naturalOverride)
    }

    @Test
    fun `ten below target is critical failure`() {
        val result = RollResolver.resolve(
            rawRoll = 10,
            modifier = -10,
            targetNumber = 10
        )

        assertEquals(0, result.finalValue)
        assertEquals(OutcomeTier.CRITICAL_FAILURE, result.outcome)
        assertEquals(NaturalOverride.NONE, result.naturalOverride)
    }

    @Test
    fun `natural 20 overrides mathematical failure`() {
        val result = RollResolver.resolve(
            rawRoll = 20,
            modifier = -5,
            targetNumber = 20
        )

        assertEquals(15, result.finalValue)
        assertEquals(OutcomeTier.CRITICAL_SUCCESS, result.outcome)
        assertEquals(NaturalOverride.NATURAL_20, result.naturalOverride)
    }

    @Test
    fun `natural 1 overrides mathematical success`() {
        val result = RollResolver.resolve(
            rawRoll = 1,
            modifier = 10,
            targetNumber = 5
        )

        assertEquals(11, result.finalValue)
        assertEquals(OutcomeTier.CRITICAL_FAILURE, result.outcome)
        assertEquals(NaturalOverride.NATURAL_1, result.naturalOverride)
    }
}
