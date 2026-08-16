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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyStore: CastHistoryStore,
    onCastClick: () -> Unit
) {
    val records = remember(historyStore) {
        historyStore.load()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onCastClick,
                    icon = { Text("◆") },
                    label = { Text("CAST") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("≡") },
                    label = { Text("HISTORY") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CAST HISTORY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (records.isEmpty()) {
                Text(
                    text = "No casts yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                records.forEachIndexed { index, record ->
                    CastHistoryRow(record = record)

                    if (index != records.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

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

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Raw ${record.rawRoll} ${formatModifier(record.modifier)} = ${record.finalValue}  •  TN ${record.targetNumber}",
            style = MaterialTheme.typography.bodyLarge
        )

        when (record.naturalOverride) {
            NaturalOverride.NATURAL_20 ->
                Text(
                    text = "NATURAL 20",
                    fontWeight = FontWeight.Bold
                )

            NaturalOverride.NATURAL_1 ->
                Text(
                    text = "NATURAL 1",
                    fontWeight = FontWeight.Bold
                )

            NaturalOverride.NONE -> Unit
        }
    }
}

private fun outcomeName(
    outcome: OutcomeTier
): String {
    return when (outcome) {
        OutcomeTier.CRITICAL_SUCCESS -> "CRITICAL SUCCESS"
        OutcomeTier.SUCCESS -> "SUCCESS"
        OutcomeTier.FAILURE -> "FAILURE"
        OutcomeTier.CRITICAL_FAILURE -> "CRITICAL FAILURE"
    }
}

private fun formatModifier(
    modifier: Int
): String {
    return if (modifier >= 0) {
        "+$modifier"
    } else {
        modifier.toString()
    }
}

private fun formatTimestamp(
    timestampMillis: Long
): String {
    val formatter = SimpleDateFormat(
        "MMM d, h:mm a",
        Locale.getDefault()
    )

    return formatter.format(Date(timestampMillis))
}
