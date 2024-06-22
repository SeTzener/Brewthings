package com.brewthings.app.ui.android.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.DashPathEffect
import android.util.AttributeSet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.Grey_Nevada
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.Clock


@SuppressLint("ViewConstructor")
class MpAndroidLineChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    chartData: ChartData?,
    selectedIndex: Int,
    private val density: Density,
    private val textSize: TextUnit,
    private var isDarkTheme: Boolean,
    private var textColor: Color,
    primaryColor: Color,
    onSelect: (Int) -> Unit,
) : LineChart(context, attrs, defStyleAttr) {
    private val highlightedRenderer get() = renderer as HighlightedLineChartRenderer

    private val sensorValueSelector = object : OnChartValueSelectedListener {
        override fun onValueSelected(entry: Entry, highlight: Highlight) {
            onSelect(entry.data as? Int ?: -1)
        }

        override fun onNothingSelected() {
            onSelect(-1)
        }
    }

    init {
        chartData?.also {
            updateDatasets(chartData)
            updateVisibleXRange()
            highlightIndex(selectedIndex)
        }

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

        setOnChartValueSelectedListener(sensorValueSelector)
    }

    fun refresh(
        chartData: ChartData?,
        selectedIndex: Int,
        isDarkTheme: Boolean,
        textColor: Color,
        primaryColor: Color,
    ) {
        this.isDarkTheme = isDarkTheme
        this.textColor = textColor
        highlightedRenderer.primaryColor = primaryColor.toArgb()

        if (chartData == null) return

        val wasEmpty = data?.dataSets?.isEmpty() ?: true
        updateDatasets(chartData)
        data.notifyDataChanged()
        notifyDataSetChanged()

        if (wasEmpty) {
            updateVisibleXRange()
        }

        highlightIndex(selectedIndex)
        invalidate()
    }

    private fun updateDatasets(chartData: ChartData) {
        data = chartData.data
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
        axisLeft.isEnabled = false
    }

    private fun updateVisibleXRange() {
        val endDate = Clock.System.now()
        val startDate = endDate - 7.days
        val visibleGraphTimePeriod = (endDate.epochSeconds - startDate.epochSeconds).toFloat()
        setVisibleXRange(visibleGraphTimePeriod, visibleGraphTimePeriod)
    }

    private fun highlightIndex(selectedIndex: Int) {
        if (selectedIndex == -1) return
        val entry = data?.dataSets?.firstOrNull()?.getEntryForIndex(selectedIndex)
        if (entry == null) {
            highlightValue(null, false)
            return
        }
        highlightValue(entry.x, entry.y, 0, false)
        moveToX(entry.x)
    }

    private fun moveToX(xValue: Float) {
        val xPadding = with(density) { 16.dp.toPx() }
        val xTarget: Float = xValue - visibleXRange * xPadding / 100
        moveViewToX(xTarget)
    }

    private fun setupHorizontalEdges() {
        setViewPortOffsets(0f, 0f, 0f, 0f)
        val dragOffsetX = with(density) { Size.Graph.DRAG_OFFSET_X.toPx() }
        setDragOffsetX(dragOffsetX)
    }
}
