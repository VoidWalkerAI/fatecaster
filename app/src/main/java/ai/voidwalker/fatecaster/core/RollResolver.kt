package ai.voidwalker.fatecaster.core

/*
============================================================
CAVECODE INSIDE — RollResolver.kt
Built against CaveCode Protocol v1.0
============================================================
*/

/*
============================================================
🪨 BLOCK 1 — AUTHORITATIVE ROLL RULES
============================================================
This block is the FateCaster resolution engine.
Treat it as read-only unless the human explicitly requests a
rules change.

Settled contract:
- rawRoll: 1 through 20
- modifier: -10 through +10
- targetNumber: 1 through 30
- finalValue = rawRoll + modifier
- natural 20 = CRITICAL SUCCESS
- natural 1 = CRITICAL FAILURE
- final >= TN + 10 = CRITICAL SUCCESS
- final >= TN = SUCCESS
- final <= TN - 10 = CRITICAL FAILURE
- otherwise = FAILURE

Layout, wording, animation, navigation, and other presentation
work must not change this block.
*/
object RollResolver {

    fun resolve(
        rawRoll: Int,
        modifier: Int,
        targetNumber: Int
    ): RollResult {

        require(rawRoll in 1..20) {
            "rawRoll must be between 1 and 20"
        }

        require(modifier in -10..10) {
            "modifier must be between -10 and +10"
        }

        require(targetNumber in 1..30) {
            "targetNumber must be between 1 and 30"
        }

        val finalValue = rawRoll + modifier

        val naturalOverride = when (rawRoll) {
            20 -> NaturalOverride.NATURAL_20
            1 -> NaturalOverride.NATURAL_1
            else -> NaturalOverride.NONE
        }

        val outcome = when {
            rawRoll == 20 ->
                OutcomeTier.CRITICAL_SUCCESS

            rawRoll == 1 ->
                OutcomeTier.CRITICAL_FAILURE

            finalValue - targetNumber >= 10 ->
                OutcomeTier.CRITICAL_SUCCESS

            finalValue >= targetNumber ->
                OutcomeTier.SUCCESS

            targetNumber - finalValue >= 10 ->
                OutcomeTier.CRITICAL_FAILURE

            else ->
                OutcomeTier.FAILURE
        }

        return RollResult(
            rawRoll = rawRoll,
            modifier = modifier,
            finalValue = finalValue,
            targetNumber = targetNumber,
            outcome = outcome,
            naturalOverride = naturalOverride
        )
    }
}
