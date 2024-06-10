package com.brewthings.app.ui.android.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.DashPathEffect
import android.util.AttributeSet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import com.brewthings.app.ui.theme.Grey_Nevada
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis

@SuppressLint("ViewConstructor")
class MpAndroidLineChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    chartData: ChartData?,
    private val density: Density,
    private val textSize: TextUnit,
    private var isDarkTheme: Boolean,
    private var textColor: Color,
    primaryColor: Color,
) : LineChart(context, attrs, defStyleAttr) {
    val highlightedRenderer: HighlightedLineChartRenderer get() = renderer as HighlightedLineChartRenderer

    init {
        chartData?.also { updateDatasets(chartData) }

        configureXAxis()

        axisRight.isEnabled = false
        axisLeft.isEnabled = false
        description.isEnabled = false
        legend.isEnabled = false

        renderer = HighlightedLineChartRenderer(
            this,
            animator,
            viewPortHandler,
            density,
            primaryColor.toArgb(),
        )
    }

    fun refresh(
        chartData: ChartData?,
        isDarkTheme: Boolean,
        textColor: Color,
        primaryColor: Color,
    ) {
        this.isDarkTheme = isDarkTheme
        this.textColor = textColor
        highlightedRenderer.primaryColor = primaryColor.toArgb()
        chartData?.also {
            updateDatasets(it)
            data.notifyDataChanged()
            notifyDataSetChanged()
        }
        invalidate()
    }

    private fun updateDatasets(chartData: ChartData) {
        data = chartData.data
    }

    private fun configureXAxis() {
        with(xAxis) {
            valueFormatter = DateValueFormatter(dateFormat = "d/M")
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(true)
            setDrawGridLinesBehindData(false)
            val dashedLineLength = with(density) { Size.Graph.DASHED_LINE_LENGTH.toPx() }
            setGridDashedLine(DashPathEffect(floatArrayOf(dashedLineLength, dashedLineLength), 0f))
            gridColor = Grey_Nevada.toArgb()
            setDrawAxisLine(false)
            textColor = this@MpAndroidLineChart.textColor.toArgb()
            textSize = this@MpAndroidLineChart.textSize.value
            position = XAxis.XAxisPosition.BOTTOM
            setXAxisRenderer(
                XAxisLabelRenderer(
                    gridLinePadding = with(density) { Size.Graph.GRID_LINE_PADDING.toPx() },
                    viewPortHandler = viewPortHandler,
                    xAxis = xAxis,
                    transformer = getTransformer(YAxis.AxisDependency.LEFT),
                )
            )
        }
    }
}
