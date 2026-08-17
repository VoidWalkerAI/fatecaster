package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.RollResult

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
