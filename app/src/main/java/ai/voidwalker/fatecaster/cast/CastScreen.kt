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
*/

/*
============================================================
🪨 BLOCK 1 — CAST PRESENTATION BOUNDARY
============================================================
This file owns the CAST screen presentation.

It may display state supplied by CastViewModel, but it must not
invent authoritative roll outcomes or write history directly.
Roll mathematics belongs in core/RollResolver.kt.
*/

/*
============================================================
🖍️ BLOCK 2 — TUNING KNOBS
============================================================
Human-facing presentation knobs for this file live here.
Change these values here instead of hunting through the screen code.
Physical phone testing is required after layout changes.
*/

// Casting feel
private const val CAST_ANIMATION_MILLIS = 700L
private const val D20_ROTATION_MILLIS = 650
private const val D20_ROTATION_DEGREES = 360f

// Main screen spacing and controls
private val SCREEN_HORIZONTAL_PADDING = 24.dp
private val SCREEN_VERTICAL_PADDING = 20.dp
private val HEADER_TO_D20_SPACING = 24.dp
private val D20_TO_MODIFIER_SPACING = 28.dp
private val CONTROL_ROW_SPACING = 12.dp
private val CONTROLS_TO_CAST_BUTTON_SPACING = 24.dp
private val CAST_BUTTON_HEIGHT = 56.dp
private val CAST_BUTTON_TO_RESULT_SPACING = 28.dp
private val RESULT_BOTTOM_SPACING = 24.dp
private val STEPPER_VALUE_HORIZONTAL_PADDING = 16.dp
private const val STEPPER_LABEL_WEIGHT = 1f

// D20 size and line work
private val D20_SIZE = 220.dp
private val D20_NUMBER_SIZE = 52.sp
private const val D20_OUTLINE_STROKE_WIDTH = 6f
private const val D20_FACET_STROKE_WIDTH = 3f

// D20 shape points, expressed as fractions of the drawing area
private const val D20_TOP_X = 0.50f
private const val D20_TOP_Y = 0.03f
private const val D20_UPPER_RIGHT_X = 0.93f
private const val D20_UPPER_RIGHT_Y = 0.30f
private const val D20_LOWER_RIGHT_X = 0.80f
private const val D20_LOWER_RIGHT_Y = 0.82f
private const val D20_BOTTOM_X = 0.50f
private const val D20_BOTTOM_Y = 0.97f
private const val D20_LOWER_LEFT_X = 0.20f
private const val D20_LOWER_LEFT_Y = 0.82f
private const val D20_UPPER_LEFT_X = 0.07f
private const val D20_UPPER_LEFT_Y = 0.30f
private const val D20_CENTER_X = 0.50f
private const val D20_CENTER_Y = 0.52f

// Result area
private val RESULT_AREA_HEIGHT = 132.dp
private val RESULT_ITEM_SPACING = 12.dp

/*
============================================================
🌐 BLOCK 3 — PLAYER-FACING TEXT
============================================================
Text and accessibility wording displayed or spoken by this screen
lives here so it can be changed without searching the UI logic.
*/
private const val TEXT_APP_TITLE = "FATECASTER"
private const val TEXT_CAST_TAB = "CAST"
private const val TEXT_HISTORY_TAB = "HISTORY"
private const val TEXT_MODIFIER = "Modifier"
private const val TEXT_TARGET_TN = "Target TN"
private const val TEXT_CAST_FATE = "CAST FATE"
private const val TEXT_AWAITING_FATE = "Awaiting your Fate…"
private const val TEXT_NATURAL_20 = "NATURAL 20"
private const val TEXT_NATURAL_1 = "NATURAL 1"
private const val TEXT_CRITICAL_SUCCESS = "CRITICAL SUCCESS"
private const val TEXT_SUCCESS = "SUCCESS"
private const val TEXT_FAILURE = "FAILURE"
private const val TEXT_CRITICAL_FAILURE = "CRITICAL FAILURE"
private const val TEXT_D20_PLACEHOLDER = "—"
private const val ICON_CAST = "◆"
private const val ICON_HISTORY = "≡"

private fun castResultText(finalValue: Int): String =
    "Your Fate has been cast: $finalValue"

private fun rawMathText(
    rawRoll: Int,
    modifier: Int,
    finalValue: Int
): String =
    "Raw $rawRoll ${formatModifier(modifier)} = $finalValue"

private fun targetText(targetNumber: Int): String =
    "TN $targetNumber"

private fun decreaseDescription(label: String): String =
    "Decrease $label"

private fun increaseDescription(label: String): String =
    "Increase $label"

private fun d20ContentDescription(rawRoll: Int?): String =
    if (rawRoll == null) {
        "D20 awaiting cast"
    } else {
        "Raw d20 result $rawRoll"
    }

private fun OutcomeTier.displayName(): String {
    return when (this) {
        OutcomeTier.CRITICAL_SUCCESS -> TEXT_CRITICAL_SUCCESS
        OutcomeTier.SUCCESS -> TEXT_SUCCESS
        OutcomeTier.FAILURE -> TEXT_FAILURE
        OutcomeTier.CRITICAL_FAILURE -> TEXT_CRITICAL_FAILURE
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

/*
============================================================
🎮 BLOCK 4 — CAST SCREEN FLOW
============================================================
Main CAST screen orchestration.

- observes CastUiState
- freezes navigation and controls while Casting
- waits CAST_ANIMATION_MILLIS before completeCast()
- renders Ready, Casting, and Result presentation
- routes HISTORY navigation through onHistoryClick
*/
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
                        Text(ICON_CAST)
                    },
                    label = {
                        Text(TEXT_CAST_TAB)
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onHistoryClick,
                    enabled = !isCasting,
                    icon = {
                        Text(ICON_HISTORY)
                    },
                    label = {
                        Text(TEXT_HISTORY_TAB)
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
                .padding(
                    horizontal = SCREEN_HORIZONTAL_PADDING,
                    vertical = SCREEN_VERTICAL_PADDING
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = TEXT_APP_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(HEADER_TO_D20_SPACING)
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
                modifier = Modifier.height(D20_TO_MODIFIER_SPACING)
            )

            StepperRow(
                label = TEXT_MODIFIER,
                value = formatModifier(state.modifier),
                decreaseEnabled =
                    !isCasting && state.modifier > -10,
                increaseEnabled =
                    !isCasting && state.modifier < 10,
                onDecrease = viewModel::decreaseModifier,
                onIncrease = viewModel::increaseModifier
            )

            Spacer(
                modifier = Modifier.height(CONTROL_ROW_SPACING)
            )

            StepperRow(
                label = TEXT_TARGET_TN,
                value = state.targetNumber.toString(),
                decreaseEnabled =
                    !isCasting && state.targetNumber > 1,
                increaseEnabled =
                    !isCasting && state.targetNumber < 30,
                onDecrease = viewModel::decreaseTargetNumber,
                onIncrease = viewModel::increaseTargetNumber
            )

            Spacer(
                modifier = Modifier.height(CONTROLS_TO_CAST_BUTTON_SPACING)
            )

            Button(
                onClick = viewModel::beginCast,
                enabled = !isCasting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CAST_BUTTON_HEIGHT)
            ) {
                Text(
                    text = TEXT_CAST_FATE,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(CAST_BUTTON_TO_RESULT_SPACING)
            )

            ResultArea(
                state = state
            )

            Spacer(
                modifier = Modifier.height(RESULT_BOTTOM_SPACING)
            )
        }
    }
}

/*
============================================================
🎮 BLOCK 5 — STEPPER CONTROL PRESENTATION
============================================================
Draws the reusable Modifier / Target TN control row.
Human-adjustable spacing for this control is kept in BLOCK 2.
*/
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
            modifier = Modifier.weight(STEPPER_LABEL_WEIGHT)
        )

        FilledTonalButton(
            onClick = onDecrease,
            enabled = decreaseEnabled,
            modifier = Modifier.semantics {
                contentDescription = decreaseDescription(label)
            }
        ) {
            Text("−")
        }

        Text(
            text = value,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = STEPPER_VALUE_HORIZONTAL_PADDING
            )
        )

        FilledTonalButton(
            onClick = onIncrease,
            enabled = increaseEnabled,
            modifier = Modifier.semantics {
                contentDescription = increaseDescription(label)
            }
        ) {
            Text("+")
        }
    }
}

/*
============================================================
🎮 BLOCK 6 — D20 PRESENTATION
============================================================
Draws and animates the faceted d20.
Human-adjustable size, timing, line widths, and shape points
are centralized in BLOCK 2.
*/
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
        targetValue = if (isCasting) D20_ROTATION_DEGREES else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = D20_ROTATION_MILLIS,
                easing = LinearEasing
            )
        ),
        label = "d20-rotation"
    )

    val displayText =
        rawRoll?.toString() ?: TEXT_D20_PLACEHOLDER

    Box(
        modifier = Modifier
            .size(D20_SIZE)
            .semantics {
                contentDescription = d20ContentDescription(rawRoll)
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
                Offset(w * D20_TOP_X, h * D20_TOP_Y)

            val upperRight =
                Offset(w * D20_UPPER_RIGHT_X, h * D20_UPPER_RIGHT_Y)

            val lowerRight =
                Offset(w * D20_LOWER_RIGHT_X, h * D20_LOWER_RIGHT_Y)

            val bottom =
                Offset(w * D20_BOTTOM_X, h * D20_BOTTOM_Y)

            val lowerLeft =
                Offset(w * D20_LOWER_LEFT_X, h * D20_LOWER_LEFT_Y)

            val upperLeft =
                Offset(w * D20_UPPER_LEFT_X, h * D20_UPPER_LEFT_Y)

            val center =
                Offset(w * D20_CENTER_X, h * D20_CENTER_Y)

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
                style = Stroke(width = D20_OUTLINE_STROKE_WIDTH)
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
                    strokeWidth = D20_FACET_STROKE_WIDTH
                )
            }

            drawLine(
                color = facetColor,
                start = upperLeft,
                end = lowerRight,
                strokeWidth = D20_FACET_STROKE_WIDTH
            )

            drawLine(
                color = facetColor,
                start = upperRight,
                end = lowerLeft,
                strokeWidth = D20_FACET_STROKE_WIDTH
            )
        }

        Text(
            text = displayText,
            fontSize = D20_NUMBER_SIZE,
            fontWeight = FontWeight.Bold
        )
    }
}

/*
============================================================
🎮 BLOCK 7 — RESULT PRESENTATION
============================================================
Displays Ready, Casting, and completed-result states.
Result-area dimensions are tuned in BLOCK 2.
Player-facing wording is centralized in BLOCK 3.
*/
@Composable
private fun ResultArea(
    state: CastUiState
) {
    when (state) {

        is CastUiState.Ready -> {
            Spacer(
                modifier = Modifier.height(RESULT_AREA_HEIGHT)
            )
        }

        is CastUiState.Casting -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(RESULT_AREA_HEIGHT),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = TEXT_AWAITING_FATE,
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
                    text = castResultText(result.finalValue),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(RESULT_ITEM_SPACING)
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
                            text = TEXT_NATURAL_20,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NaturalOverride.NATURAL_1 -> {
                        Text(
                            text = TEXT_NATURAL_1,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NaturalOverride.NONE -> Unit
                }

                Spacer(
                    modifier = Modifier.height(RESULT_ITEM_SPACING)
                )

                Text(
                    text = rawMathText(
                        rawRoll = result.rawRoll,
                        modifier = result.modifier,
                        finalValue = result.finalValue
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = targetText(result.targetNumber),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
