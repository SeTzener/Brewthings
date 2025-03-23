package com.brewthings.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun BrewCompositionChart(
    abvPercentage: Float,
    sweetnessPercentage: Float,
    modifier: Modifier = Modifier
) {
    val totalPercentage = abvPercentage + sweetnessPercentage
    val remainingPercentage = 100f - totalPercentage // Water in the middle

    var animateStart by remember { mutableStateOf(false) }
    val animatedSweetness = animateFloatAsState(
        targetValue = if (animateStart) sweetnessPercentage else 0f,
        animationSpec = tween(1000)
    )
    val animatedWater = animateFloatAsState(
        targetValue = if (animateStart) remainingPercentage else 0f,
        animationSpec = tween(1000, delayMillis = 1000)
    )
    val animatedAbv = animateFloatAsState(
        targetValue = if (animateStart) abvPercentage else 0f,
        animationSpec = tween(1000, delayMillis = 2000)
    )

    LaunchedEffect(Unit) {
        animateStart = true
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val glassStrokeWidth = 8.dp

        // Function to calculate width at a specific height (to match the trapezoidal shape)
        fun getWidthAtHeight(fillHeight: Float): Float {
            val topWidth = width * 0.9f
            val bottomWidth = width * 0.6f
            val interpolation = fillHeight / height // 0.0 at bottom, 1.0 at top
            return bottomWidth + (topWidth - bottomWidth) * interpolation
        }

        // Function to create trapezoidal path
        fun createTrapezoidPath(
            startHeight: Float,
            fillHeight: Float,
            paddingValues: PaddingValues = PaddingValues()
        ): Path {
            val bottomWidthAtStart = getWidthAtHeight(startHeight)
            val topWidthAtEnd = getWidthAtHeight(startHeight + fillHeight)

            val paddingBottomPx = paddingValues.calculateBottomPadding().toPx()
            val paddingTopPx = paddingValues.calculateTopPadding().toPx()
            val paddingLeftPx = paddingValues.calculateStartPadding(LayoutDirection.Ltr).toPx()
            val paddingRightPx = paddingValues.calculateEndPadding(LayoutDirection.Ltr).toPx()

            val leftBottom = (width - bottomWidthAtStart) / 2 + paddingLeftPx
            val rightBottom = leftBottom + bottomWidthAtStart - 2 * paddingRightPx
            val leftTop = (width - topWidthAtEnd) / 2 + paddingLeftPx
            val rightTop = leftTop + topWidthAtEnd - 2 * paddingRightPx

            val bottom = height - startHeight - paddingBottomPx
            val top = fillHeight
                .takeIf { it > 0f }
                ?.let { bottom - it + paddingTopPx }
                ?: bottom

            return Path().apply {
                moveTo(leftBottom, bottom) // Start at the bottom of the liquid
                lineTo(leftTop, top) // Move to the top of the liquid
                lineTo(rightTop, top) // Top edge
                lineTo(rightBottom, bottom) // Bottom edge
                close()
            }
        }

        // Draw the glass with trapezoidal shape
        val glassPath = createTrapezoidPath(0f, height)

        // Draw glass outline with rounded corners
        drawPath(
            path = glassPath,
            color = Color.Gray,
            style = Stroke(
                width = glassStrokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Compute actual heights (each layer builds on top of the previous one)
        val sweetnessHeight = (animatedSweetness.value / 100f) * height
        val waterHeight = (animatedWater.value / 100f) * height
        val abvHeight = (animatedAbv.value / 100f) * height

        // Draw the liquid layers from bottom to top
        fun drawLiquid(
            startHeight: Float,
            fillHeight: Float,
            color: Color,
            paddingValues: PaddingValues
        ) {
            val path = createTrapezoidPath(startHeight, fillHeight, paddingValues)
            // Draw the liquid fill
            drawPath(
                path = path,
                color = color,
                style = Fill
            )
        }

        // Correct drawing order (bottom to top)
        val glassPadding = glassStrokeWidth / 2
        drawLiquid(
            0f,
            sweetnessHeight,
            Color(0xFFFFC107),
            PaddingValues(
                start = glassPadding,
                end = glassPadding,
                top = 0.dp,
                bottom = glassPadding,
            )
        ) // Sweetness (Amber)
        drawLiquid(
            sweetnessHeight,
            waterHeight,
            Color(0xFFB3E5FC),
            PaddingValues(
                start = glassPadding,
                end = glassPadding,
                top = 0.dp,
                bottom = 0.dp,
            )
        ) // Water (Light Blue)
        drawLiquid(
            sweetnessHeight + waterHeight,
            abvHeight,
            Color(0xFFB71C1C),
            PaddingValues(
                start = glassPadding,
                end = glassPadding,
                top = glassPadding,
                bottom = 0.dp,
            )
        ) // Alcohol (Dark Red)
    }
}

@Composable
@Preview
fun BrewCompositionChartPreview() {
    BrewthingsTheme {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BrewCompositionChart(
                    modifier = Modifier.height(500.dp),
                    abvPercentage = 40f,
                    sweetnessPercentage = 20f,
                )
            }
        }
    }
}