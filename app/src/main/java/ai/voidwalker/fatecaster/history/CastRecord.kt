package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import ai.voidwalker.fatecaster.core.RollResult

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
