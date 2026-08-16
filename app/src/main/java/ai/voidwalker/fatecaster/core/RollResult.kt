package ai.voidwalker.fatecaster.core

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
