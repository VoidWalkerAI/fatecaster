package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files

class CastHistoryStoreTest {

    @Test
    fun `newest cast is stored first`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-test")
                .resolve("history.txt")
                .toFile()

        val store = CastHistoryStore(historyFile)

        store.add(record(timestamp = 1000L))
        store.add(record(timestamp = 2000L))

        val history = store.load()

        assertEquals(2, history.size)
        assertEquals(2000L, history[0].timestampMillis)
        assertEquals(1000L, history[1].timestampMillis)
    }

    @Test
    fun `history survives reopening store`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-test")
                .resolve("history.txt")
                .toFile()

        val firstStore = CastHistoryStore(historyFile)

        firstStore.add(record(timestamp = 1234L))

        val reopenedStore = CastHistoryStore(historyFile)
        val history = reopenedStore.load()

        assertEquals(1, history.size)
        assertEquals(1234L, history[0].timestampMillis)
    }

    @Test
    fun `history retains only most recent 100 casts`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-test")
                .resolve("history.txt")
                .toFile()

        val store = CastHistoryStore(historyFile)

        for (timestamp in 1L..101L) {
            store.add(record(timestamp))
        }

        val history = store.load()

        assertEquals(100, history.size)

        assertEquals(
            101L,
            history.first().timestampMillis
        )

        assertEquals(
            2L,
            history.last().timestampMillis
        )
    }

    @Test
    fun `clear removes all retained casts`() {
        val historyFile =
            Files.createTempDirectory("fatecaster-test")
                .resolve("history.txt")
                .toFile()

        val store = CastHistoryStore(historyFile)

        store.add(record(timestamp = 1000L))
        store.add(record(timestamp = 2000L))

        store.clear()

        assertTrue(store.load().isEmpty())
    }

    private fun record(
        timestamp: Long
    ): CastRecord {
        return CastRecord(
            timestampMillis = timestamp,
            rawRoll = 10,
            modifier = 2,
            finalValue = 12,
            targetNumber = 10,
            outcome = OutcomeTier.SUCCESS,
            naturalOverride = NaturalOverride.NONE
        )
    }
}
