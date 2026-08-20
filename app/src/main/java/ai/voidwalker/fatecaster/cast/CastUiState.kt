package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.RollResult

/*
============================================================
CAVECODE INSIDE — CastUiState.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / STATE CONTRACT
Purpose:
- Defines the three CAST screen states: Ready, Casting, Result.
- Carries modifier and target number through the CAST flow.
- Carries RollResult only after a cast has completed.

🎮 BLOCK 2 — STATE FLOW
Expected progression:
Ready → Casting → Result
A later cast may move Result → Casting → Result.

🪨 BLOCK 3 — SETTLED DEFAULTS
Ready starts at:
- modifier = 0
- targetNumber = 10
These are product defaults, not casual tuning knobs.

🖍️ BLOCK 4 — HUMAN EDIT ZONE
No human-safe tuning values are designated in this file.

🌐 BLOCK 5 — PUBLIC TEXT
None. This file is state only and must not own player-facing wording.

AI EDIT RULE:
Keep this file as a small data/state contract. Do not put roll resolution,
persistence, navigation, or presentation logic here.
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
