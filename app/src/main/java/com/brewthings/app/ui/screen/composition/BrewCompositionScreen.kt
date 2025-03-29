package com.brewthings.app.ui.screen.composition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.ui.component.TopAppBarBackButton
import com.brewthings.app.ui.component.TopAppBarTitle
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.Gold
import com.brewthings.app.ui.theme.GoldDark
import com.brewthings.app.ui.theme.LimeGreen
import com.brewthings.app.ui.theme.LimeGreenDark
import com.brewthings.app.ui.theme.SteelBlue
import com.brewthings.app.ui.theme.SteelBlueDark
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewCompositionScreen(
    state: BrewCompositionScreenState,
    colors: BrewCompositionColors = BrewCompositionColors.default(),
    onBackClick: () -> Unit,
) {
    val totalPercentage = state.abvPercentage + state.sweetnessPercentage
    val remainingPercentage = 100f - totalPercentage

    var animateStart by remember { mutableStateOf(false) }

    val animatedSweetness by animateFloatAsState(
        targetValue = if (animateStart) state.sweetnessPercentage else 0f,
        animationSpec = tween(1000)
    )
    val animatedSweetnessVisibility by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0f,
        animationSpec = tween(1000)
    )
    val animatedAbv by animateFloatAsState(
        targetValue = if (animateStart) state.abvPercentage else 0f,
        animationSpec = tween(1000, delayMillis = 1000)
    )
    val animatedAbvVisibility by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 1000)
    )
    val animatedWater by animateFloatAsState(
        targetValue = if (animateStart) remainingPercentage else 0f,
        animationSpec = tween(1000, delayMillis = 2000)
    )
    val animatedWaterVisibility by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 2000)
    )

    LaunchedEffect(Unit) {
        animateStart = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { TopAppBarBackButton(onBackClick) },
                title = { TopAppBarTitle(stringResource(R.string.brew_composition_title)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrewCompositionChart(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                abvPercentage = animatedAbv,
                sweetnessPercentage = animatedSweetness,
                waterPercentage = animatedWater,
                colors = colors,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompositionBar(
                    label = stringResource(R.string.brew_composition_sweetness),
                    percentage = animatedSweetness,
                    color = colors.sweetness,
                )

                Text(
                    modifier = Modifier.alpha(animatedSweetnessVisibility),
                    text = state.sweetnessPercentage.toSweetnessLevelDescription(),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                CompositionBar(
                    label = stringResource(R.string.brew_composition_abv),
                    percentage = animatedAbv,
                    color = colors.abv,
                )

                Text(
                    modifier = Modifier.alpha(animatedAbvVisibility),
                    text = state.abvPercentage.toAbvLevelDescription(),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                CompositionBar(
                    label = stringResource(R.string.brew_composition_water),
                    percentage = animatedWater,
                    color = colors.water,
                )

                Text(
                    modifier = Modifier.alpha(animatedWaterVisibility),
                    text = state.abvPercentage.toWaterLevelDescription(),
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}

@Composable
private fun CompositionBar(label: String, percentage: Float, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.brew_composition_percent_format, percentage.toInt()),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BrewCompositionChart(
    modifier: Modifier = Modifier,
    abvPercentage: Float,
    waterPercentage: Float,
    sweetnessPercentage: Float,
    colors: BrewCompositionColors,
    strokeWidth: Dp = 12.dp,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val height = size.height
        val width = min(size.width, 1.5f * size.height)
        val startWidth = size.width - width
        val strokePadding = strokeWidth / 2

        // Compute actual heights (each layer builds on top of the previous one)
        val sweetnessHeight = (sweetnessPercentage / 100f) * height
        val waterHeight = (waterPercentage / 100f) * height
        val abvHeight = (abvPercentage / 100f) * height

        // Function to calculate width at a specific height (to match the trapezoidal shape)
        fun getWidthAtHeight(fillHeight: Float): Float {
            val bottomWidth = width * 0.6f
            val interpolation = fillHeight / height // 0.0 at bottom, 1.0 at top
            return bottomWidth + (width - bottomWidth) * interpolation
        }

        // Function to create a trapezoidal path
        fun createTrapezoidPath(
            startHeight: Float,
            fillHeight: Float,
            paddings: PaddingValues = PaddingValues(0.dp),
        ): Path {
            val bottomWidthAtStart = getWidthAtHeight(startHeight)
            val topWidthAtEnd = getWidthAtHeight(startHeight + fillHeight)

            val paddingBottom = paddings.calculateBottomPadding().toPx()
            val paddingTop = paddings.calculateTopPadding().toPx()
            val paddingLeft =
                paddings.calculateStartPadding(LayoutDirection.Ltr).toPx() + startWidth
            val paddingRight = paddings.calculateEndPadding(LayoutDirection.Ltr).toPx()

            val leftBottom = (width - bottomWidthAtStart) / 2
            val rightBottom = leftBottom + bottomWidthAtStart
            val leftTop = (width - topWidthAtEnd) / 2
            val rightTop = leftTop + topWidthAtEnd - 2

            val bottom = height - startHeight
            val top = bottom - fillHeight
            val paddedTop = if (fillHeight < paddingTop) top else top + paddingTop
            val paddedBottom = (bottom - paddingBottom).coerceAtLeast(paddedTop)

            return Path().apply {
                // Start at the bottom of the liquid
                moveTo(leftBottom + paddingLeft, paddedBottom)
                // Move to the top of the liquid
                lineTo(leftTop + paddingLeft, paddedTop)
                // Top edge
                lineTo(rightTop - paddingRight, paddedTop)
                // Bottom edge
                lineTo(rightBottom - paddingRight, paddedBottom)
                close()
            }
        }

        // Function to draw a filled trapezoidal path
        fun drawFillPath(
            color: Color,
            startHeight: Float,
            fillHeight: Float,
            paddings: PaddingValues,
        ) {
            drawPath(
                path = createTrapezoidPath(startHeight, fillHeight, paddings),
                color = color,
                style = Fill,
            )
        }

        // Draw glass outline with rounded corners
        drawPath(
            path = createTrapezoidPath(0f, height),
            color = Color.Gray,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw the fill layers
        drawFillPath(
            color = colors.sweetness,
            startHeight = 0f,
            fillHeight = sweetnessHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
                bottom = strokePadding,
            ),
        )

        drawFillPath(
            color = colors.abv,
            startHeight = sweetnessHeight,
            fillHeight = abvHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
            ),
        )

        drawFillPath(
            color = colors.water,
            startHeight = sweetnessHeight + abvHeight,
            fillHeight = waterHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
                top = strokePadding,
            ),
        )
    }
}

@Composable
private fun Float.toSweetnessLevelDescription(): String {
    val level = when {
        this <= 3f -> stringResource(R.string.brew_composition_sweetness_level_0_3)
        this <= 5f -> stringResource(R.string.brew_composition_sweetness_level_3_5)
        this <= 10f -> stringResource(R.string.brew_composition_sweetness_level_5_10)
        this <= 15f -> stringResource(R.string.brew_composition_sweetness_level_10_15)
        this <= 20f -> stringResource(R.string.brew_composition_sweetness_level_15_20)
        this <= 30f -> stringResource(R.string.brew_composition_sweetness_level_20_30)
        this <= 40f -> stringResource(R.string.brew_composition_sweetness_level_30_40)
        this <= 50f -> stringResource(R.string.brew_composition_sweetness_level_40_50)
        this <= 60f -> stringResource(R.string.brew_composition_sweetness_level_50_60)
        this <= 70f -> stringResource(R.string.brew_composition_sweetness_level_60_70)
        this <= 80f -> stringResource(R.string.brew_composition_sweetness_level_70_80)
        this <= 90f -> stringResource(R.string.brew_composition_sweetness_level_80_90)
        else -> stringResource(R.string.brew_composition_sweetness_level_90_100)
    }
    return stringResource(R.string.brew_composition_sweetness_level, level)
}

@Composable
private fun Float.toAbvLevelDescription(): String {
    val level = when {
        this <= 2f -> stringResource(R.string.brew_composition_abv_level_0_2)
        this <= 5f -> stringResource(R.string.brew_composition_abv_level_2_5)
        this <= 7f -> stringResource(R.string.brew_composition_abv_level_5_7)
        this <= 10f -> stringResource(R.string.brew_composition_abv_level_7_10)
        this <= 12f -> stringResource(R.string.brew_composition_abv_level_10_12)
        this <= 14f -> stringResource(R.string.brew_composition_abv_level_12_14)
        this <= 16f -> stringResource(R.string.brew_composition_abv_level_14_16)
        this <= 18f -> stringResource(R.string.brew_composition_abv_level_16_18)
        this <= 20f -> stringResource(R.string.brew_composition_abv_level_18_20)
        this <= 35f -> stringResource(R.string.brew_composition_abv_level_20_35)
        this <= 50f -> stringResource(R.string.brew_composition_abv_level_35_50)
        this <= 70f -> stringResource(R.string.brew_composition_abv_level_50_70)
        else -> stringResource(R.string.brew_composition_abv_level_70_100)
    }
    return stringResource(R.string.brew_composition_abv_level, level)
}

@Composable
private fun Float.toWaterLevelDescription(): String = when {
    this <= 10f -> stringResource(R.string.brew_composition_water_level_0_10)
    this <= 30f -> stringResource(R.string.brew_composition_water_level_10_30)
    this <= 50f -> stringResource(R.string.brew_composition_water_level_30_50)
    this <= 70f -> stringResource(R.string.brew_composition_water_level_50_70)
    this <= 90f -> stringResource(R.string.brew_composition_water_level_70_90)
    else -> stringResource(R.string.brew_composition_water_level_90_100)
}

data class BrewCompositionColors(
    val stroke: Color,
    val abv: Color,
    val sweetness: Color,
    val water: Color,
) {
    companion object {
        @Composable
        fun default() = if (isSystemInDarkTheme()) {
            BrewCompositionColors(
                stroke = MaterialTheme.colorScheme.onSurface,
                abv = LimeGreenDark,
                sweetness = GoldDark,
                water = SteelBlueDark,
            )
        } else {
            BrewCompositionColors(
                stroke = MaterialTheme.colorScheme.onSurface,
                abv = LimeGreen,
                sweetness = Gold,
                water = SteelBlue,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun BrewCompositionScreenPreview() {
    BrewthingsTheme {
        Surface {
            BrewCompositionScreen(
                state = BrewCompositionScreenState(
                    abvPercentage = 40f,
                    sweetnessPercentage = 20f
                ),
                onBackClick = {},
            )
        }
    }
}
