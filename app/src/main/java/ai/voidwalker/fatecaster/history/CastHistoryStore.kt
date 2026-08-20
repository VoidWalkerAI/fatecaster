package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import java.io.File

/*
============================================================
CAVECODE INSIDE — CastHistoryStore.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / LOCAL PERSISTENCE
Purpose:
- Loads saved cast records from the app-private history file.
- Adds newest records first.
- Retains no more than 100 records.
- Clears saved history on explicit request.
- Encodes and decodes the current tab-separated storage format.

🎮 BLOCK 2 — PERSISTENCE FLOW
load()    → file text → CastRecord list
add()     → prepend record → cap at MAX_RECORDS → persist
clear()   → empty existing history file
persist() → CastRecord list → file text

🪨 BLOCK 3 — LOCKED STORAGE CONTRACT
MAX_RECORDS = 100 is the settled FC-008 history limit.
The encoded field sequence must stay compatible with CastRecord:
1. timestampMillis
2. rawRoll
3. modifier
4. finalValue
5. targetNumber
6. outcome
7. naturalOverride

Changing this shape can make previously saved history unreadable.

🖍️ BLOCK 4 — HUMAN EDIT ZONE
None. MAX_RECORDS and the file format are settled behavior, not casual
configuration knobs.

🌐 BLOCK 5 — PUBLIC TEXT
None. This file stores data only and must not own HISTORY wording.

AI EDIT RULE:
Treat persistence/schema changes as high-impact. UI requests such as spacing,
colors, labels, or layout must not modify this file unless the requested
behavior actually requires a storage change.
*/

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
