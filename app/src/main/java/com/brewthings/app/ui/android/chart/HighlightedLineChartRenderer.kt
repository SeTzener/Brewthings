package com.brewthings.app.ui.android.chart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style
import androidx.compose.ui.unit.Density
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.renderer.LineChartRenderer

class HighlightedLineChartRenderer(
    chart: LineChart,
    chartAnimator: ChartAnimator,
    private val density: Density,
    var primaryColor: Int,
) : LineChartRenderer(chart, chartAnimator, chart.viewPortHandler) {

    private val highlightPaint: Paint = Paint().apply {
        strokeWidth = with(density) { Size.Graph.CIRCLE_RADIUS.toPx() * 1.5f }
        style = Style.STROKE
    }

    private val dottedLinePaint: Paint = Paint().apply {
        strokeWidth = 1f // Customize the dotted line width
        style = Style.STROKE
        pathEffect = with(density) { Size.Graph.DASHED_LINE_LENGTH.toPx() }.let {
            DashPathEffect(floatArrayOf(it, it), 0f) // Customize the dash effect
        }
    }

    private val highlighterPadding = with(density) { Size.Graph.HIGHLIGHTER_LINE_PADDING.toPx() }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        for (high in indices) {
            val lineData = mChart.lineData ?: return

            val hDataSet = lineData.getDataSetByIndex(high.dataSetIndex)
            for (dataSet in lineData.dataSets) {
                if (!dataSet.isHighlightEnabled) continue

                val trans = mChart.getTransformer(dataSet.axisDependency)

                highlightPaint.color = dataSet.color
                highlightPaint.setShadowLayer(
                    with(density) { Size.Graph.CIRCLE_RADIUS.toPx() * 4 },
                    0f,
                    0f,
                    dataSet.color,
                )

                dataSet.getEntriesForXValue(high.x)?.firstOrNull()?.also { entry ->
                    val pts = floatArrayOf(entry.x, entry.y)
                    trans.pointValuesToPixel(pts)

                    if (dataSet == hDataSet) {
                        dottedLinePaint.color = primaryColor
                        // Drawing a vertical dotted line at the highlighted points
                        c.drawLine(
                            pts[0],
                            mViewPortHandler.contentTop() + highlighterPadding,
                            pts[0],
                            mViewPortHandler.contentBottom() - highlighterPadding,
                            dottedLinePaint,
                        )
                    }

                    // Drawing a custom circle with shadow at the highlighted point
                    c.drawCircle(pts[0], pts[1], 10f, highlightPaint)
                }
            }
        }
    }
}
