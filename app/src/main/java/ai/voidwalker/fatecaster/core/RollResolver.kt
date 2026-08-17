package ai.voidwalker.fatecaster.core

object RollResolver {

    fun resolve(
        rawRoll: Int,
        modifier: Int,
        targetNumber: Int
    ): RollResult {

        require(rawRoll in 1..20) {
            "rawRoll must be between 1 and 20"
        }

        require(modifier in -10..10) {
            "modifier must be between -10 and +10"
        }

        require(targetNumber in 1..30) {
            "targetNumber must be between 1 and 30"
        }

        val finalValue = rawRoll + modifier

        val naturalOverride = when (rawRoll) {
            20 -> NaturalOverride.NATURAL_20
            1 -> NaturalOverride.NATURAL_1
            else -> NaturalOverride.NONE
        }

        val outcome = when {
            rawRoll == 20 ->
                OutcomeTier.CRITICAL_SUCCESS

            rawRoll == 1 ->
                OutcomeTier.CRITICAL_FAILURE

            finalValue - targetNumber >= 10 ->
                OutcomeTier.CRITICAL_SUCCESS

            finalValue >= targetNumber ->
                OutcomeTier.SUCCESS

            targetNumber - finalValue >= 10 ->
                OutcomeTier.CRITICAL_FAILURE

            else ->
                OutcomeTier.FAILURE
        }

        return RollResult(
            rawRoll = rawRoll,
            modifier = modifier,
            finalValue = finalValue,
            targetNumber = targetNumber,
            outcome = outcome,
            naturalOverride = naturalOverride
        )
    }
}
