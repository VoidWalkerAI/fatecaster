package ai.voidwalker.fatecaster

import ai.voidwalker.fatecaster.cast.CastScreen
import ai.voidwalker.fatecaster.cast.CastViewModel
import ai.voidwalker.fatecaster.history.CastHistoryStore
import ai.voidwalker.fatecaster.history.HistoryScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.io.File

/*
============================================================
CAVECODE INSIDE — MainActivity.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / APPLICATION SHELL
Purpose:
- Starts FateCaster.
- Creates the local CastHistoryStore and CastViewModel.
- Owns the top-level CAST ↔ HISTORY destination switch.

This file wires existing parts together. It does NOT own roll mathematics,
history encoding, or screen-specific presentation rules.

🎮 BLOCK 2 — APPLICATION FLOW
The app currently has two destinations only:
- CAST
- HISTORY

Navigation changes which screen is visible. It must not alter roll results
or stored history.

🖍️ BLOCK 3 — HUMAN EDIT ZONE
No ordinary tuning knobs live in this file right now.
Do not move product rules here just to make them easier to edit.

🌐 BLOCK 4 — PUBLIC TEXT
No player-facing copy is intentionally owned here. Public wording belongs
with the screen that displays it.

🪨 BLOCK 5 — LOCKED BOUNDARIES
- Roll resolution belongs in core/RollResolver.kt.
- Cast state/orchestration belongs in cast/CastViewModel.kt.
- History persistence belongs in history/CastHistoryStore.kt.
- CAST and HISTORY presentation belong in their screen files.

AI EDIT RULE:
For wiring/navigation tasks, edit only the smallest required section.
Do not rewrite downstream product rules while changing app-shell behavior.
*/

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val historyStore = CastHistoryStore(
            historyFile = File(filesDir, "cast-history.tsv")
        )

        val castViewModel = CastViewModel(
            historyStore = historyStore
        )

        setContent {
            FateCasterApp(
                viewModel = castViewModel,
                historyStore = historyStore
            )
        }
    }
}

private enum class FateCasterDestination {
    CAST,
    HISTORY
}

@Composable
fun FateCasterApp(
    viewModel: CastViewModel,
    historyStore: CastHistoryStore
) {
    var destination by remember {
        mutableStateOf(FateCasterDestination.CAST)
    }

    MaterialTheme {
        when (destination) {
            FateCasterDestination.CAST -> {
                CastScreen(
                    viewModel = viewModel,
                    onHistoryClick = {
                        destination = FateCasterDestination.HISTORY
                    }
                )
            }

            FateCasterDestination.HISTORY -> {
                HistoryScreen(
                    historyStore = historyStore,
                    onCastClick = {
                        destination = FateCasterDestination.CAST
                    }
                )
            }
        }
    }
}
