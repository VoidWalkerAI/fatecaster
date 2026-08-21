package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import ai.voidwalker.fatecaster.core.RollResult

/*
============================================================
CAVECODE INSIDE — CastRecord.kt
Built against CaveCode Protocol v1.0
============================================================
*/

/*
============================================================
🪨 BLOCK 1 — HISTORY RECORD CONTRACT
============================================================
Defines the durable record copied from a completed RollResult.
The field meanings must remain compatible with CastHistoryStore.

This is not a human tuning area. Persistence-format changes must
be deliberate because they can affect existing saved history.
*/
data class CastRecord(
    val timestampMillis: Long,
    val rawRoll: Int,
    val modifier: Int,
    val finalValue: Int,
    val targetNumber: Int,
    val outcome: OutcomeTier,
    val naturalOverride: NaturalOverride
) {
    companion object {

        fun from(
            result: RollResult,
            timestampMillis: Long
        ): CastRecord {
            return CastRecord(
                timestampMillis = timestampMillis,
                rawRoll = result.rawRoll,
                modifier = result.modifier,
                finalValue = result.finalValue,
                targetNumber = result.targetNumber,
                outcome = result.outcome,
                naturalOverride = result.naturalOverride
            )
        }
    }
}
