package ai.voidwalker.fatecaster.history

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
============================================================
CAVECODE INSIDE — HistoryScreen.kt
Built against CaveCode Protocol v1.0
============================================================
*/

/*
============================================================
🪨 BLOCK 1 — HISTORY PRESENTATION BOUNDARY
============================================================
This file presents stored CastRecord entries and requests history
actions. It does not define roll mathematics or the storage schema.
*/

/*
============================================================
🖍️ BLOCK 2 — TUNING KNOBS
============================================================
Human-facing presentation knobs for this file live here.
Change these values here instead of hunting through the screen code.
*/
private val SCREEN_HORIZONTAL_PADDING = 24.dp
private val SCREEN_VERTICAL_PADDING = 20.dp
private val HEADER_BOTTOM_SPACING = 20.dp
private val EMPTY_STATE_LINE_SPACING = 6.dp
private val ROW_META_SPACING = 6.dp
private val ROW_DIVIDER_VERTICAL_PADDING = 14.dp
private const val TIMESTAMP_PATTERN = "MMM d, h:mm a"

/*
============================================================
🌐 BLOCK 3 — PLAYER-FACING TEXT
============================================================
Text displayed by HISTORY lives here so wording can be changed
without searching through the screen behavior.
*/
private const val TEXT_HISTORY_TITLE = "CAST HISTORY"
private const val TEXT_CLEAR_HISTORY = "Clear History"
private const val TEXT_CLEAR_DIALOG_TITLE = "Clear history?"
private const val TEXT_CLEAR_DIALOG_MESSAGE = "This will remove all saved casts."
private const val TEXT_CANCEL = "Cancel"
private const val TEXT_CLEAR = "Clear"
private const val TEXT_EMPTY_TITLE = "No casts yet."
private const val TEXT_EMPTY_SUBTITLE = "Cast your fate to begin."
private const val TEXT_CAST_TAB = "CAST"
private const val TEXT_HISTORY_TAB = "HISTORY"
private const val TEXT_NATURAL_20 = "NATURAL 20"
private const val TEXT_NATURAL_1 = "NATURAL 1"
private const val TEXT_CRITICAL_SUCCESS = "CRITICAL SUCCESS"
private const val TEXT_SUCCESS = "SUCCESS"
private const val TEXT_FAILURE = "FAILURE"
private const val TEXT_CRITICAL_FAILURE = "CRITICAL FAILURE"
private const val ICON_CAST = "◆"
private const val ICON_HISTORY = "≡"

private fun outcomeName(
    outcome: OutcomeTier
): String {
    return when (outcome) {
        OutcomeTier.CRITICAL_SUCCESS -> TEXT_CRITICAL_SUCCESS
        OutcomeTier.SUCCESS -> TEXT_SUCCESS
        OutcomeTier.FAILURE -> TEXT_FAILURE
        OutcomeTier.CRITICAL_FAILURE -> TEXT_CRITICAL_FAILURE
    }
}

private fun recordMathText(record: CastRecord): String =
    "Raw ${record.rawRoll} ${formatModifier(record.modifier)} = ${record.finalValue}  •  TN ${record.targetNumber}"

private fun formatModifier(
    modifier: Int
): String {
    return if (modifier >= 0) {
        "+$modifier"
    } else {
        modifier.toString()
    }
}

/*
============================================================
🎮 BLOCK 4 — HISTORY SCREEN FLOW
============================================================
- loads records from CastHistoryStore
- keeps Clear History disabled when empty
- requires confirmation before clearing
- Cancel preserves records
- Clear removes records and immediately reloads the empty state
- keeps CAST / HISTORY navigation available
*/
@Composable
fun HistoryScreen(
    historyStore: CastHistoryStore,
    onCastClick: () -> Unit
) {
    var records by remember(historyStore) {
        mutableStateOf(historyStore.load())
    }
    var showClearConfirmation by remember {
        mutableStateOf(false)
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showClearConfirmation = false
            },
            title = {
                Text(TEXT_CLEAR_DIALOG_TITLE)
            },
            text = {
                Text(TEXT_CLEAR_DIALOG_MESSAGE)
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                    }
                ) {
                    Text(TEXT_CANCEL)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        historyStore.clear()
                        records = historyStore.load()
                        showClearConfirmation = false
                    }
                ) {
                    Text(TEXT_CLEAR)
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onCastClick,
                    icon = { Text(ICON_CAST) },
                    label = { Text(TEXT_CAST_TAB) }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text(ICON_HISTORY) },
                    label = { Text(TEXT_HISTORY_TAB) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = SCREEN_HORIZONTAL_PADDING,
                    vertical = SCREEN_VERTICAL_PADDING
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TEXT_HISTORY_TITLE,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    enabled = records.isNotEmpty(),
                    onClick = {
                        showClearConfirmation = true
                    }
                ) {
                    Text(TEXT_CLEAR_HISTORY)
                }
            }

            Spacer(modifier = Modifier.height(HEADER_BOTTOM_SPACING))

            if (records.isEmpty()) {
                Text(
                    text = TEXT_EMPTY_TITLE,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(EMPTY_STATE_LINE_SPACING))

                Text(
                    text = TEXT_EMPTY_SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                records.forEachIndexed { index, record ->
                    CastHistoryRow(record = record)

                    if (index != records.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                vertical = ROW_DIVIDER_VERTICAL_PADDING
                            )
                        )
                    }
                }
            }
        }
    }
}

/*
============================================================
🎮 BLOCK 5 — HISTORY ROW PRESENTATION
============================================================
Displays one read-only CastRecord.
Spacing is tuned in BLOCK 2 and wording is centralized in BLOCK 3.
*/
@Composable
private fun CastHistoryRow(
    record: CastRecord
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = outcomeName(record.outcome),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatTimestamp(record.timestampMillis),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(ROW_META_SPACING))

        Text(
            text = recordMathText(record),
            style = MaterialTheme.typography.bodyLarge
        )

        when (record.naturalOverride) {
            NaturalOverride.NATURAL_20 ->
                Text(
                    text = TEXT_NATURAL_20,
                    fontWeight = FontWeight.Bold
                )

            NaturalOverride.NATURAL_1 ->
                Text(
                    text = TEXT_NATURAL_1,
                    fontWeight = FontWeight.Bold
                )

            NaturalOverride.NONE -> Unit
        }
    }
}

/*
============================================================
🎮 BLOCK 6 — TIMESTAMP FORMATTER
============================================================
Converts stored epoch time into the local display timestamp.
The human-editable timestamp pattern is in BLOCK 2.
*/
private fun formatTimestamp(
    timestampMillis: Long
): String {
    val formatter = SimpleDateFormat(
        TIMESTAMP_PATTERN,
        Locale.getDefault()
    )

    return formatter.format(Date(timestampMillis))
}
