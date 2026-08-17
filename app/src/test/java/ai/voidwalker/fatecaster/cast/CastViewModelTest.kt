package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import ai.voidwalker.fatecaster.history.CastHistoryStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files

class CastViewModelTest {

    @Test
    fun `default state is ready with modifier zero and TN ten`() {
        val viewModel = viewModel()

        val state = viewModel.uiState as CastUiState.Ready

        assertEquals(0, state.modifier)
        assertEquals(10, state.targetNumber)
    }

    @Test
    fun `modifier and target controls stop at their boundaries`() {
        val viewModel = viewModel()

        repeat(20) {
            viewModel.decreaseModifier()
        }

        repeat(40) {
            viewModel.decreaseTargetNumber()
        }

        assertEquals(-10, viewModel.uiState.modifier)
        assertEquals(1, viewModel.uiState.targetNumber)

        repeat(40) {
            viewModel.increaseModifier()
        }

        repeat(60) {
            viewModel.increaseTargetNumber()
        }

        assertEquals(10, viewModel.uiState.modifier)
        assertEquals(30, viewModel.uiState.targetNumber)
    }

    @Test
    fun `controls are frozen while casting`() {
        val viewModel = viewModel()

        viewModel.increaseModifier()
        viewModel.increaseTargetNumber()

        viewModel.beginCast()

        viewModel.increaseModifier()
        viewModel.decreaseModifier()
        viewModel.increaseTargetNumber()
        viewModel.decreaseTargetNumber()

        val state = viewModel.uiState as CastUiState.Casting

        assertEquals(1, state.modifier)
        assertEquals(11, state.targetNumber)
    }

    @Test
    fun `completed cast resolves once and writes one history record`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-cast-test")
                .resolve("history.txt")
                .toFile()

        val historyStore = CastHistoryStore(historyFile)

        val viewModel = CastViewModel(
            historyStore = historyStore,
            rollD20 = { 20 },
            nowMillis = { 1234L }
        )

        repeat(5) {
            viewModel.decreaseModifier()
        }

        repeat(10) {
            viewModel.increaseTargetNumber()
        }

        viewModel.beginCast()
        viewModel.completeCast()

        val state = viewModel.uiState as CastUiState.Result

        assertEquals(20, state.rollResult.rawRoll)
        assertEquals(-5, state.rollResult.modifier)
        assertEquals(15, state.rollResult.finalValue)
        assertEquals(20, state.rollResult.targetNumber)

        assertEquals(
            OutcomeTier.CRITICAL_SUCCESS,
            state.rollResult.outcome
        )

        assertEquals(
            NaturalOverride.NATURAL_20,
            state.rollResult.naturalOverride
        )

        val history = historyStore.load()

        assertEquals(1, history.size)
        assertEquals(1234L, history[0].timestampMillis)
        assertEquals(20, history[0].rawRoll)
        assertEquals(-5, history[0].modifier)
        assertEquals(15, history[0].finalValue)
        assertEquals(20, history[0].targetNumber)
    }

    @Test
    fun `complete cast cannot create duplicate history records`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-cast-test")
                .resolve("history.txt")
                .toFile()

        val historyStore = CastHistoryStore(historyFile)

        var rollCount = 0

        val viewModel = CastViewModel(
            historyStore = historyStore,
            rollD20 = {
                rollCount += 1
                10
            },
            nowMillis = { 2000L }
        )

        viewModel.beginCast()

        viewModel.completeCast()
        viewModel.completeCast()

        assertEquals(1, rollCount)
        assertEquals(1, historyStore.load().size)
    }

    @Test
    fun `changing controls after result preserves completed result`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-cast-test")
                .resolve("history.txt")
                .toFile()

        val viewModel = CastViewModel(
            historyStore = CastHistoryStore(historyFile),
            rollD20 = { 17 },
            nowMillis = { 3000L }
        )

        repeat(2) {
            viewModel.increaseModifier()
        }

        repeat(5) {
            viewModel.increaseTargetNumber()
        }

        viewModel.beginCast()
        viewModel.completeCast()

        viewModel.increaseModifier()
        viewModel.increaseTargetNumber()

        val state = viewModel.uiState as CastUiState.Result

        // Controls now describe the NEXT cast.
        assertEquals(3, state.modifier)
        assertEquals(16, state.targetNumber)

        // The completed result still describes the LAST cast.
        assertEquals(17, state.rollResult.rawRoll)
        assertEquals(2, state.rollResult.modifier)
        assertEquals(19, state.rollResult.finalValue)
        assertEquals(15, state.rollResult.targetNumber)
        assertEquals(OutcomeTier.SUCCESS, state.rollResult.outcome)
    }

    private fun viewModel(): CastViewModel {
        val historyFile =
            Files.createTempDirectory("fatecaster-cast-test")
                .resolve("history.txt")
                .toFile()

        return CastViewModel(
            historyStore = CastHistoryStore(historyFile),
            rollD20 = { 10 },
            nowMillis = { 1000L }
        )
    }
}
