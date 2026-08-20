package ai.voidwalker.fatecaster.cast

import ai.voidwalker.fatecaster.core.NaturalOverride
import ai.voidwalker.fatecaster.core.OutcomeTier
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/*
============================================================
CAVECODE INSIDE — CastScreen.kt
Built against CaveCode Protocol v1.0
============================================================

🪨 BLOCK 1 — FILE IDENTITY / CAST PRESENTATION
Purpose:
- Presents the CAST screen and bottom navigation.
- Displays the d20, modifier, TN, CAST FATE control, casting state, and result.
- Converts settled result/state data into player-visible presentation.

This file must not decide authoritative roll outcomes or write history itself.

🖍️ BLOCK 2 — HUMAN EDIT ZONE / TUNING KNOBS
Safe presentation territory includes visual spacing, sizing, typography, and
animation feel when the request is explicitly visual.

Current named tuning knob:
- CAST_ANIMATION_MILLIS = 700L
  Controls how long the casting presentation waits before completeCast().
  Changing it changes feel/timing only; it must not change roll mathematics.

Many layout dimensions are still inline Compose dp/sp values. They are
presentation values, but are not yet centralized as named tuning knobs.
Physical phone testing is required after layout changes.

🌐 BLOCK 3 — PLAYER-FACING TEXT
Public wording owned here includes:
- FATECASTER
- CAST / HISTORY
- Modifier
- Target TN
- CAST FATE
- Awaiting your Fate…
- Your Fate has been cast: ...
- CRITICAL SUCCESS / SUCCESS / FAILURE / CRITICAL FAILURE
- NATURAL 20 / NATURAL 1
- Raw ... / TN ... result wording

Wording can be intentionally revised without changing resolver rules.

🎮 BLOCK 4 — SCREEN BEHAVIOR
- Observe CastUiState from CastViewModel.
- While Casting, disable controls/navigation and show casting presentation.
- After CAST_ANIMATION_MILLIS, request viewModel.completeCast().
- Render Ready, Casting, and Result states.
- Route HISTORY navigation through onHistoryClick.

🪨 BLOCK 5 — LOCKED BOUNDARIES
- Roll mathematics belongs in core/RollResolver.kt.
- Modifier/TN state transitions and cast orchestration belong in
  CastViewModel.kt.
- History persistence belongs in history/CastHistoryStore.kt.

AI EDIT RULE:
For viewport-fit, spacing, typography, animation, or wording requests, stay
inside this presentation territory unless there is clear evidence that the
requested behavior requires a state-flow change. Do not edit RollResolver to
solve a screen-layout problem.
*/

private const val CAST_ANIMATION_MILLIS = 700L

@Composable
fun CastScreen(
    viewModel: CastViewModel,
    onHistoryClick: () -> Unit
) {
    val state = viewModel.uiState
    val isCasting = state is CastUiState.Casting

    LaunchedEffect(state) {
        if (state is CastUiState.Casting) {
            delay(CAST_ANIMATION_MILLIS)
            viewModel.completeCast()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    enabled = !isCasting,
                    icon = {
                        Text("◆")
                    },
                    label = {
                        Text("CAST")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onHistoryClick,
                    enabled = !isCasting,
                    icon = {
                        Text("≡")
                    },
                    label = {
                        Text("HISTORY")
                    }
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
                text = "FATECASTER",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            D20Display(
                rawRoll = when (state) {
                    is CastUiState.Result ->
                        state.rollResult.rawRoll

                    else ->
                        null
                },
                isCasting = isCasting
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            StepperRow(
                label = "Modifier",
                value = formatModifier(state.modifier),
                decreaseEnabled =
                    !isCasting && state.modifier > -10,
                increaseEnabled =
                    !isCasting && state.modifier < 10,
                onDecrease = viewModel::decreaseModifier,
                onIncrease = viewModel::increaseModifier
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            StepperRow(
                label = "Target TN",
                value = state.targetNumber.toString(),
                decreaseEnabled =
                    !isCasting && state.targetNumber > 1,
                increaseEnabled =
                    !isCasting && state.targetNumber < 30,
                onDecrease = viewModel::decreaseTargetNumber,
                onIncrease = viewModel::increaseTargetNumber
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = viewModel::beginCast,
                enabled = !isCasting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "CAST FATE",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            ResultArea(
                state = state
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        FilledTonalButton(
            onClick = onDecrease,
            enabled = decreaseEnabled,
            modifier = Modifier.semantics {
                contentDescription = "Decrease $label"
            }
        ) {
            Text("−")
        }

        Text(
            text = value,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        FilledTonalButton(
            onClick = onIncrease,
            enabled = increaseEnabled,
            modifier = Modifier.semantics {
                contentDescription = "Increase $label"
            }
        ) {
            Text("+")
        }
    }
}

@Composable
private fun D20Display(
    rawRoll: Int?,
    isCasting: Boolean
) {
    val outlineColor =
        MaterialTheme.colorScheme.primary

    val facetColor =
        MaterialTheme.colorScheme.outlineVariant

    val transition =
        rememberInfiniteTransition(
            label = "d20-casting"
        )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (isCasting) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 650,
                easing = LinearEasing
            )
        ),
        label = "d20-rotation"
    )

    val displayText =
        rawRoll?.toString() ?: "—"

    Box(
        modifier = Modifier
            .size(220.dp)
            .semantics {
                contentDescription =
                    if (rawRoll == null) {
                        "D20 awaiting cast"
                    } else {
                        "Raw d20 result $rawRoll"
                    }
            },
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            val w = size.width
            val h = size.height

            val top =
                Offset(w * 0.50f, h * 0.03f)

            val upperRight =
                Offset(w * 0.93f, h * 0.30f)

            val lowerRight =
                Offset(w * 0.80f, h * 0.82f)

            val bottom =
                Offset(w * 0.50f, h * 0.97f)

            val lowerLeft =
                Offset(w * 0.20f, h * 0.82f)

            val upperLeft =
                Offset(w * 0.07f, h * 0.30f)

            val center =
                Offset(w * 0.50f, h * 0.52f)

            val outline = Path().apply {
                moveTo(top.x, top.y)
                lineTo(upperRight.x, upperRight.y)
                lineTo(lowerRight.x, lowerRight.y)
                lineTo(bottom.x, bottom.y)
                lineTo(lowerLeft.x, lowerLeft.y)
                lineTo(upperLeft.x, upperLeft.y)
                close()
            }

            drawPath(
                path = outline,
                color = outlineColor,
                style = Stroke(width = 6f)
            )

            listOf(
                top,
                upperRight,
                lowerRight,
                bottom,
                lowerLeft,
                upperLeft
            ).forEach { point ->
                drawLine(
                    color = facetColor,
                    start = center,
                    end = point,
                    strokeWidth = 3f
                )
            }

            drawLine(
                color = facetColor,
                start = upperLeft,
                end = lowerRight,
                strokeWidth = 3f
            )

            drawLine(
                color = facetColor,
                start = upperRight,
                end = lowerLeft,
                strokeWidth = 3f
            )
        }

        Text(
            text = displayText,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ResultArea(
    state: CastUiState
) {
    when (state) {

        is CastUiState.Ready -> {
            Spacer(
                modifier = Modifier.height(132.dp)
            )
        }

        is CastUiState.Casting -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Awaiting your Fate…",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        is CastUiState.Result -> {
            val result = state.rollResult

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "Your Fate has been cast: ${result.finalValue}",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = result.outcome.displayName(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                when (result.naturalOverride) {

                    NaturalOverride.NATURAL_20 -> {
                        Text(
                            text = "NATURAL 20",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NaturalOverride.NATURAL_1 -> {
                        Text(
                            text = "NATURAL 1",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NaturalOverride.NONE -> Unit
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Raw ${result.rawRoll} " +
                        "${formatModifier(result.modifier)} " +
                        "= ${result.finalValue}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "TN ${result.targetNumber}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

private fun OutcomeTier.displayName(): String {
    return when (this) {
        OutcomeTier.CRITICAL_SUCCESS ->
            "CRITICAL SUCCESS"

        OutcomeTier.SUCCESS ->
            "SUCCESS"

        OutcomeTier.FAILURE ->
            "FAILURE"

        OutcomeTier.CRITICAL_FAILURE ->
            "CRITICAL FAILURE"
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
