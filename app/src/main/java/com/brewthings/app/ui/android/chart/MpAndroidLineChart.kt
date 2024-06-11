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
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.Clock

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
    private val highlightedRenderer get() = renderer as HighlightedLineChartRenderer

    init {
        chartData?.also { updateDatasets(chartData) }

        configureXAxis()
        configureYAxis()
        setupHorizontalEdges()

        description.isEnabled = false
        legend.isEnabled = false

        renderer = HighlightedLineChartRenderer(
            this,
            animator,
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
        updateVisibleXRange()
        highlightLast()
        // Note: all moveViewTo(...) methods will automatically invalidate() (refresh) the chart. There is no need for
        // further calling invalidate().
    }

    private fun updateDatasets(chartData: ChartData) {
        data = chartData.data
        updateYAxisVisibility()
    }

    private fun configureXAxis() {
        with(xAxis) {
            isEnabled = true
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
                    xAxis = this,
                    transformer = getTransformer(YAxis.AxisDependency.LEFT),
                )
            )
        }
    }

    private fun configureYAxis() {
        axisRight.isEnabled = false

        with(axisLeft) {
            setDrawGridLines(false)
            setDrawGridLinesBehindData(false)
            setDrawAxisLine(false)
            textColor = this@MpAndroidLineChart.textColor.toArgb()
            textSize = this@MpAndroidLineChart.textSize.value
            xOffset = with(density) { Size.Graph.GRID_LINE_PADDING.toPx() }
        }
    }

    private fun updateYAxisVisibility() {
        axisLeft.isEnabled = data.dataSets.size == 1
    }

    private fun updateVisibleXRange() {
        val endDate = Clock.System.now()
        val startDate = endDate - 7.days
        val visibleGraphTimePeriod = (endDate.epochSeconds - startDate.epochSeconds).toFloat()
        setVisibleXRange(visibleGraphTimePeriod, visibleGraphTimePeriod)
        moveViewToX(endDate.epochSeconds.toFloat())
    }

    private fun highlightLast() {
        val lastX = Clock.System.now().epochSeconds.toFloat()
        data?.dataSets?.firstOrNull()?.getEntryForXValue(lastX, Float.NaN)?.let {
            highlightValue(it.x, it.y, 0, true)
        }
    }

    private fun setupHorizontalEdges() {
        setViewPortOffsets(0f, 0f, 0f, 0f)
        val dragOffsetX = with(density) { Size.Graph.DRAG_OFFSET_X.toPx() }
        setDragOffsetX(dragOffsetX)
    }
}
