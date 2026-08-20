package ai.voidwalker.fatecaster.core

/*
============================================================
CAVECODE INSIDE — RollResult.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / RESULT CONTRACT
Purpose:
- Defines the authoritative outcome names used by FateCaster.
- Defines natural-roll override labels.
- Defines the complete resolved roll payload passed to UI and history.

🪨 BLOCK 2 — LOCKED DATA SHAPE
OutcomeTier, NaturalOverride, and RollResult are shared contracts.
Changes can affect resolver logic, CAST presentation, HISTORY persistence,
and stored-record decoding.

🖍️ BLOCK 3 — HUMAN EDIT ZONE
None. These names and fields are not presentation tuning knobs.

🌐 BLOCK 4 — PUBLIC TEXT
None directly. Screens may translate these enum values into player-facing
labels without changing the enum contract.

AI EDIT RULE:
Do not rename, remove, or reinterpret result fields during UI-only work.
Any schema change must be reviewed together with RollResolver,
CastRecord, CastHistoryStore, and affected tests.
*/

enum class OutcomeTier {
    CRITICAL_SUCCESS,
    SUCCESS,
    FAILURE,
    CRITICAL_FAILURE
}

enum class NaturalOverride {
    NONE,
    NATURAL_1,
    NATURAL_20
}

data class RollResult(
    val rawRoll: Int,
    val modifier: Int,
    val finalValue: Int,
    val targetNumber: Int,
    val outcome: OutcomeTier,
    val naturalOverride: NaturalOverride
)
