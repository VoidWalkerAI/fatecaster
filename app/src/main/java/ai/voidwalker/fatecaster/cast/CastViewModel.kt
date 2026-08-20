package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.RollResolver
import ai.voidwalker.fatecaster.history.CastHistoryStore
import ai.voidwalker.fatecaster.history.CastRecord
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

/*
============================================================
CAVECODE INSIDE — CastViewModel.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / CAST ORCHESTRATION
Purpose:
- Owns CAST screen state transitions.
- Owns modifier and TN control updates.
- Starts and completes a cast.
- Requests one d20 value.
- Sends inputs to RollResolver.
- Converts the resolved result into CastRecord and saves it.

This file coordinates the cast. It does NOT decide the outcome mathematics.

🎮 BLOCK 2 — CAST FLOW
beginCast()
    Ready/Result → Casting

completeCast()
    Casting
    → roll one d20
    → RollResolver.resolve(...)
    → CastRecord.from(...)
    → CastHistoryStore.add(...)
    → Result

🪨 BLOCK 3 — SETTLED CONTROL BOUNDS
- modifier is clamped to -10..+10
- target number is clamped to 1..30
- controls do not change while Casting
These are settled product rules, not presentation tuning knobs.

🔧 BLOCK 4 — TEST / INJECTION SEAMS
rollD20 and nowMillis are injected so tests can supply deterministic values.
Do not replace these seams with UI logic or hidden global state.

🖍️ BLOCK 5 — HUMAN EDIT ZONE
None. This file has no designated casual tuning knobs.

🌐 BLOCK 6 — PUBLIC TEXT
None. Player-facing wording belongs in CastScreen.kt.

🪨 BLOCK 7 — LOCKED BOUNDARIES
- Outcome mathematics belongs only in core/RollResolver.kt.
- Persistence format belongs only in history/CastHistoryStore.kt.
- CAST layout/animation/copy belongs in cast/CastScreen.kt.

AI EDIT RULE:
For UI-only requests, treat this file as read-only unless the requested
interaction genuinely requires a state-flow change.
*/

class CastViewModel(
    private val historyStore: CastHistoryStore,
    private val rollD20: () -> Int = {
        Random.nextInt(from = 1, until = 21)
    },
    private val nowMillis: () -> Long = {
        System.currentTimeMillis()
    }
) : ViewModel() {

    var uiState by mutableStateOf<CastUiState>(
        CastUiState.Ready()
    )
        private set

    fun decreaseModifier() {
        updateControls(
            modifierDelta = -1
        )
    }

    fun increaseModifier() {
        updateControls(
            modifierDelta = 1
        )
    }

    fun decreaseTargetNumber() {
        updateControls(
            targetDelta = -1
        )
    }

    fun increaseTargetNumber() {
        updateControls(
            targetDelta = 1
        )
    }

    fun beginCast() {
        val current = uiState

        if (current is CastUiState.Casting) {
            return
        }

        uiState = CastUiState.Casting(
            modifier = current.modifier,
            targetNumber = current.targetNumber
        )
    }

    fun completeCast() {
        val castingState =
            uiState as? CastUiState.Casting
                ?: return

        val rawRoll = rollD20()

        val result = RollResolver.resolve(
            rawRoll = rawRoll,
            modifier = castingState.modifier,
            targetNumber = castingState.targetNumber
        )

        val record = CastRecord.from(
            result = result,
            timestampMillis = nowMillis()
        )

        historyStore.add(record)

        uiState = CastUiState.Result(
            modifier = castingState.modifier,
            targetNumber = castingState.targetNumber,
            rollResult = result
        )
    }

    private fun updateControls(
        modifierDelta: Int = 0,
        targetDelta: Int = 0
    ) {
        val current = uiState

        if (current is CastUiState.Casting) {
            return
        }

        val newModifier =
            (current.modifier + modifierDelta)
                .coerceIn(-10, 10)

        val newTarget =
            (current.targetNumber + targetDelta)
                .coerceIn(1, 30)

        uiState = when (current) {

            is CastUiState.Ready ->
                current.copy(
                    modifier = newModifier,
                    targetNumber = newTarget
                )

            is CastUiState.Result ->
                current.copy(
                    modifier = newModifier,
                    targetNumber = newTarget
                )

            is CastUiState.Casting ->
                current
        }
    }
}
