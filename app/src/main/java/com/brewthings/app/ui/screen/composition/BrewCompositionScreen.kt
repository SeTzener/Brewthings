package com.brewthings.app.ui.screen.composition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.BrewthingsTheme
import kotlin.math.min

@Composable
fun BrewCompositionScreen(
    abvPercentage: Float,
    sweetnessPercentage: Float,
    modifier: Modifier = Modifier
) {
    val totalPercentage = abvPercentage + sweetnessPercentage
    val remainingPercentage = 100f - totalPercentage

    var animateStart by remember { mutableStateOf(false) }

    val animatedSweetness by animateFloatAsState(
        targetValue = if (animateStart) sweetnessPercentage else 0f,
        animationSpec = tween(1000)
    )
    val animatedSweetnessVisibility by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0f,
        animationSpec = tween(1000)
    )
    val animatedAbv by animateFloatAsState(
        targetValue = if (animateStart) abvPercentage else 0f,
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

    LaunchedEffect(Unit) {
        animateStart = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        BrewCompositionChart(
            modifier = Modifier
                .weight(1f),
            abvPercentage = animatedAbv,
            sweetnessPercentage = animatedSweetness,
            waterPercentage = animatedWater,
            colors = BrewCompositionChartColors(
                abv = Color(0xFFB71C1C),
                sweetness = Color(0xFFFFC107),
                water = Color(0xFFB3E5FC),
                stroke = Color.Gray,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompositionBar("Sweetness", animatedSweetness, Color(0xFFFFC107))

            Text(
                modifier = Modifier.alpha(animatedSweetnessVisibility),
                text = "Sweet like honey",
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            CompositionBar("Alcohol", animatedAbv, Color(0xFFB71C1C))

            Text(
                modifier = Modifier.alpha(animatedAbvVisibility),
                text = "Strong like mead",
                color = Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            CompositionBar("Water & Other Compounds", animatedWater, Color(0xFFB3E5FC))
        }
    }
}

@Composable
fun CompositionBar(label: String, percentage: Float, color: Color) {
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

        Text("${percentage.toInt()}%", textAlign = TextAlign.Center)
    }
}

@Composable
fun BrewCompositionChart(
    modifier: Modifier = Modifier,
    abvPercentage: Float,
    waterPercentage: Float,
    sweetnessPercentage: Float,
    colors: BrewCompositionChartColors,
    strokeWidth: Dp = 12.dp,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val height = size.height
        val width = min(size.width, size.height)
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
            val paddingLeft = paddings.calculateStartPadding(LayoutDirection.Ltr).toPx() + startWidth
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

data class BrewCompositionChartColors(
    val stroke: Color,
    val abv: Color,
    val sweetness: Color,
    val water: Color,
)

@Preview
@Composable
fun BrewCompositionScreenPreview() {
    BrewthingsTheme {
        Surface {
            BrewCompositionScreen(
                abvPercentage = 40f,
                sweetnessPercentage = 20f,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
