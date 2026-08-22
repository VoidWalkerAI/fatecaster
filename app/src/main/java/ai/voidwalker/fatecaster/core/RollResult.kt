package ai.voidwalker.fatecaster.core

/*
============================================================
CAVECODE INSIDE — RollResult.kt
Built against CaveCode Protocol v1.0
============================================================
*/

/*
============================================================
🪨 BLOCK 1 — RESOLVED ROLL DATA CONTRACT
============================================================
Defines the shared result vocabulary and payload used by the
resolver, CAST screen, history record, and persistence layer.

Do not rename, remove, reorder, or reinterpret these fields as
part of UI-only work. A schema change can affect several files
and previously stored history.
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
