package ai.voidwalker.fatecaster.core

/*
============================================================
CAVECODE INSIDE — RollResolver.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / AUTHORITATIVE RESOLUTION CORE
Purpose:
- Validates the settled d20 inputs.
- Calculates finalValue = rawRoll + modifier.
- Applies natural 20 / natural 1 overrides.
- Resolves the final outcome tier against TN.

This file is the authoritative rules engine for a FateCaster roll.

🎮 BLOCK 2 — SETTLED GAME LOGIC
Current contract:
- rawRoll: 1..20
- modifier: -10..+10
- targetNumber: 1..30
- natural 20 = CRITICAL SUCCESS
- natural 1 = CRITICAL FAILURE
- final >= TN + 10 = CRITICAL SUCCESS
- final >= TN = SUCCESS
- final <= TN - 10 = CRITICAL FAILURE
- otherwise = FAILURE

🪨 BLOCK 3 — LOCKED BOUNDARY
Do not alter this file for layout, wording, animation, navigation, or other
presentation work. Those changes belong elsewhere.

🖍️ BLOCK 4 — HUMAN EDIT ZONE
None. The numeric ranges and outcome thresholds are settled product rules,
not tuning knobs.

🌐 BLOCK 5 — PUBLIC TEXT
None. Error strings here are developer validation messages, not player copy.

AI EDIT RULE:
Treat this file as read-only unless the human explicitly requests a rules
change. Any requested change must preserve tests or update them deliberately
as part of the same rules decision.
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
