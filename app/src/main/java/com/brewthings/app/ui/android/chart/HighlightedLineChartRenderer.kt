package com.brewthings.app.ui.android.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class HighlightedLineChartRenderer(
    chart: LineChart,
    chartAnimator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, chartAnimator, viewPortHandler) {

    private val highlightPaint: Paint = Paint().apply {
        color = Color.WHITE /* Your custom color */
        strokeWidth = 5f // Customize the highlight line width
        style = Style.STROKE
        setShadowLayer(10f, 0f, 0f, Color.BLACK /* Shadow color */)
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        for (high in indices) {
            val lineData = mChart.lineData ?: return

            val dataSet = lineData.getDataSetByIndex(high.dataSetIndex)
            if (!dataSet.isHighlightEnabled) continue

            val entry = dataSet.getEntryForXValue(high.x, high.y)
            if (entry == null || entry.y != high.y) continue

            val trans = mChart.getTransformer(dataSet.axisDependency)

            val pts = floatArrayOf(high.x, high.y)
            trans.pointValuesToPixel(pts)

            // Drawing a custom circle with shadow at the highlighted point
            c.drawCircle(pts[0], pts[1], 10f, highlightPaint)
        }
    }
}
