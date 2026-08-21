package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.RollResult

/*
============================================================
CAVECODE INSIDE — CastUiState.kt
Built against CaveCode Protocol v1.0
============================================================
*/

/*
============================================================
🪨 BLOCK 1 — CAST STATE CONTRACT
============================================================
Defines the three CAST states and the data each state carries.

Settled defaults:
- Ready modifier = 0
- Ready targetNumber = 10

These defaults are product rules, not human tuning knobs.
*/
sealed interface CastUiState {

    val modifier: Int
    val targetNumber: Int

    data class Ready(
        override val modifier: Int = 0,
        override val targetNumber: Int = 10
    ) : CastUiState

    data class Casting(
        override val modifier: Int,
        override val targetNumber: Int
    ) : CastUiState

    data class Result(
        override val modifier: Int,
        override val targetNumber: Int,
        val rollResult: RollResult
    ) : CastUiState
}
