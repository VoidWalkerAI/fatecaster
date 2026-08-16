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
