package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.RollResolver
import ai.voidwalker.fatecaster.history.CastHistoryStore
import ai.voidwalker.fatecaster.history.CastRecord
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

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
