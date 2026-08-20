package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import ai.voidwalker.fatecaster.core.RollResult

/*
============================================================
CAVECODE INSIDE — CastRecord.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / HISTORY RECORD CONTRACT
Purpose:
- Defines the durable history record created from a resolved roll.
- Preserves the resolved values needed to reconstruct HISTORY later.

🎮 BLOCK 2 — RECORD CREATION
CastRecord.from(...) copies a completed RollResult plus timestamp into the
persistence shape used by CastHistoryStore.

🪨 BLOCK 3 — LOCKED BOUNDARY
The field order and meanings must remain compatible with
CastHistoryStore encode/decode behavior. A persistence-format change is
not a screen-polish task.

🖍️ BLOCK 4 — HUMAN EDIT ZONE
None. Stored record fields are not tuning knobs.

🌐 BLOCK 5 — PUBLIC TEXT
None. HISTORY presentation decides how these values are shown.

AI EDIT RULE:
Do not add, remove, reorder, or reinterpret persisted fields unless the
history storage format and migration consequences are explicitly part of
the requested change.
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
