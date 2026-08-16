package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import java.io.File

class CastHistoryStore(
    private val historyFile: File
) {

    companion object {
        const val MAX_RECORDS = 100
        private const val FIELD_SEPARATOR = "\t"
    }

    fun load(): List<CastRecord> {
        if (!historyFile.exists()) {
            return emptyList()
        }

        return historyFile
            .readLines()
            .mapNotNull(::decode)
            .take(MAX_RECORDS)
    }

    fun add(record: CastRecord): List<CastRecord> {
        val updatedRecords =
            (listOf(record) + load())
                .take(MAX_RECORDS)

        persist(updatedRecords)

        return updatedRecords
    }

    fun clear() {
        if (historyFile.exists()) {
            historyFile.writeText("")
        }
    }

    private fun persist(records: List<CastRecord>) {
        historyFile.parentFile?.mkdirs()

        val contents = records.joinToString(
            separator = "\n",
            transform = ::encode
        )

        historyFile.writeText(contents)
    }

    private fun encode(record: CastRecord): String {
        return listOf(
            record.timestampMillis,
            record.rawRoll,
            record.modifier,
            record.finalValue,
            record.targetNumber,
            record.outcome.name,
            record.naturalOverride.name
        ).joinToString(FIELD_SEPARATOR)
    }

    private fun decode(line: String): CastRecord? {
        if (line.isBlank()) {
            return null
        }

        val fields = line.split(FIELD_SEPARATOR)

        if (fields.size != 7) {
            return null
        }

        return runCatching {
            CastRecord(
                timestampMillis = fields[0].toLong(),
                rawRoll = fields[1].toInt(),
                modifier = fields[2].toInt(),
                finalValue = fields[3].toInt(),
                targetNumber = fields[4].toInt(),
                outcome = OutcomeTier.valueOf(fields[5]),
                naturalOverride = NaturalOverride.valueOf(fields[6])
            )
        }.getOrNull()
    }
}
