package ai.voidwalker.fatecaster

import ai.voidwalker.fatecaster.cast.CastScreen
import ai.voidwalker.fatecaster.cast.CastViewModel
import ai.voidwalker.fatecaster.history.CastHistoryStore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
                viewModel = castViewModel
            )
        }
    }
}

@Composable
fun FateCasterApp(
    viewModel: CastViewModel
) {
    MaterialTheme {
        CastScreen(
            viewModel = viewModel,
            onHistoryClick = { }
        )
    }
}
