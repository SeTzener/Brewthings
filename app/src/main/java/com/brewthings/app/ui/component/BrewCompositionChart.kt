package com.brewthings.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun BrewCompositionChart(
    modifier: Modifier = Modifier,
    abvPercentage: Float,
    sweetnessPercentage: Float,
    colors: BrewCompositionChartColors,
    strokeWidth: Dp = 8.dp,
) {
    val totalPercentage = abvPercentage + sweetnessPercentage
    val remainingPercentage = 100f - totalPercentage

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
        val strokePadding = strokeWidth / 2

        // Function to calculate width at a specific height (to match the trapezoidal shape)
        fun getWidthAtHeight(fillHeight: Float): Float {
            val bottomWidth = width * 0.6f
            val interpolation = fillHeight / height // 0.0 at bottom, 1.0 at top
            return bottomWidth + (width - bottomWidth) * interpolation
        }

        // Function to create trapezoidal path
        fun createTrapezoidPath(
            startHeight: Float,
            fillHeight: Float,
            paddings: PaddingValues = PaddingValues(0.dp),
        ): Path {
            if (fillHeight == 0f) return Path()

            val bottomWidthAtStart = getWidthAtHeight(startHeight)
            val topWidthAtEnd = getWidthAtHeight(startHeight + fillHeight)

            val paddingBottom = paddings.calculateBottomPadding().toPx()
            val paddingTop = paddings.calculateTopPadding().toPx()
            val paddingLeft = paddings.calculateStartPadding(LayoutDirection.Ltr).toPx()
            val paddingRight = paddings.calculateEndPadding(LayoutDirection.Ltr).toPx()

            val leftBottom = (width - bottomWidthAtStart) / 2
            val rightBottom = leftBottom + bottomWidthAtStart
            val leftTop = (width - topWidthAtEnd) / 2
            val rightTop = leftTop + topWidthAtEnd - 2

            val bottom = height - startHeight
            val top = bottom - fillHeight

            return Path().apply {
                // Start at the bottom of the liquid
                moveTo(leftBottom + paddingLeft, bottom - paddingBottom)
                // Move to the top of the liquid
                lineTo(leftTop + paddingLeft, top + paddingTop)
                // Top edge
                lineTo(rightTop - paddingRight, top + paddingTop)
                // Bottom edge
                lineTo(rightBottom - paddingRight, bottom - paddingBottom)
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
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Compute actual heights (each layer builds on top of the previous one)
        val sweetnessHeight = (animatedSweetness.value / 100f) * height
        val waterHeight = (animatedWater.value / 100f) * height
        val abvHeight = (animatedAbv.value / 100f) * height

        val sweetnessFillPath = createTrapezoidPath(
            startHeight = 0f,
            fillHeight = sweetnessHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
                bottom = strokePadding
            )
        )

        // Draw the sweetness
        drawPath(
            path = sweetnessFillPath,
            color = colors.sweetness,
            style = Fill
        )

        val waterFillPath = createTrapezoidPath(
            startHeight = sweetnessHeight,
            fillHeight = waterHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
            )
        )

        // Draw the sweetness
        drawPath(
            path = waterFillPath,
            color = colors.water,
            style = Fill
        )

        val abvFillPath = createTrapezoidPath(
            startHeight = sweetnessHeight + waterHeight,
            fillHeight = abvHeight,
            paddings = PaddingValues(
                start = strokePadding,
                end = strokePadding,
                top = strokePadding,
            )
        )

        // Draw the abv
        drawPath(
            path = abvFillPath,
            color = colors.abv,
            style = Fill
        )
    }
}

data class BrewCompositionChartColors(
    val stroke: Color,
    val abv: Color,
    val sweetness: Color,
    val water: Color,
) {
    companion object {
        val Default = BrewCompositionChartColors(
            stroke = Color.Gray,
            sweetness = Color(0xFFFFC107),
            abv = Color(0xFF81C784),
            water = Color(0xFFB3E5FC),
        )
    }
}

@Composable
@Preview
fun BrewCompositionChartPreview() {
    BrewthingsTheme {
        Surface {
            BrewCompositionChart(
                modifier = Modifier
                    .height(500.dp)
                    .padding(4.dp),
                abvPercentage = 40f,
                sweetnessPercentage = 20f,
                colors = BrewCompositionChartColors.Default,
            )
        }
    }
}