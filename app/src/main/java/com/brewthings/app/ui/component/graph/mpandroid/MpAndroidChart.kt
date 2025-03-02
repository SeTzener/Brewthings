package com.brewthings.app.ui.component.graph.mpandroid

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
import com.brewthings.app.util.Logger
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kotlin.time.Duration.Companion.days

@SuppressLint("ViewConstructor")
class MpAndroidChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    chartData: MpAndroidChartData?,
    selectedIndex: Int?,
    private val density: Density,
    private val textSize: TextUnit,
    private var textColor: Color,
    primaryColor: Color,
    onSelect: (Int?) -> Unit,
) : LineChart(context, attrs, defStyleAttr) {
    private val logger = Logger("MpAndroidChart")
    private val highlightedRenderer get() = renderer as HighlightedLineChartRenderer

    private var previousHighlightedIndex: Int? = null

    private val sensorValueSelector = object : OnChartValueSelectedListener {
        override fun onValueSelected(entry: Entry, highlight: Highlight) {
            onSelect(entry.data as? Int)
        }

        override fun onNothingSelected() {
            onSelect(null)
        }
    }

    init {
        Utils.init(context)

        chartData?.also {
            updateDatasets(chartData)
            updateVisibleXRange(chartData)
            highlightIndex(selectedIndex, animated = false)
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
        chartData: MpAndroidChartData?,
        selectedIndex: Int?,
        textColor: Color,
        primaryColor: Color,
    ) {
        this.textColor = textColor
        highlightedRenderer.primaryColor = primaryColor.toArgb()

        if (chartData == null) return

        val wasEmpty = data?.dataSets?.isEmpty() ?: true
        updateDatasets(chartData)
        data.notifyDataChanged()
        notifyDataSetChanged()

        if (wasEmpty) {
            updateVisibleXRange(chartData)
        }

        highlightIndex(selectedIndex, animated = true)
        invalidate()
    }

    private fun updateDatasets(chartData: MpAndroidChartData) {
        data = chartData
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
            textColor = this@MpAndroidChart.textColor.toArgb()
            textSize = this@MpAndroidChart.textSize.value
            position = XAxis.XAxisPosition.BOTTOM
            setXAxisRenderer(
                XAxisLabelRenderer(
                    gridLinePadding = with(density) { Size.Graph.GRID_LINE_PADDING.toPx() },
                    viewPortHandler = viewPortHandler,
                    xAxis = this,
                    transformer = getTransformer(YAxis.AxisDependency.LEFT),
                ),
            )
        }
    }

    private fun configureYAxis() {
        axisRight.isEnabled = false
        axisLeft.isEnabled = false
    }

    private fun updateVisibleXRange(chartData: MpAndroidChartData) {
        val endDate = chartData.to
        val startDate = maxOf(
            chartData.from,
            endDate - 7.days
        )
        val visibleGraphTimePeriod = (endDate.epochSeconds - startDate.epochSeconds).toFloat()
        setVisibleXRange(visibleGraphTimePeriod, visibleGraphTimePeriod)
    }

    private fun entryForIndex(index: Int): Entry? {
        data?.dataSets?.forEach { dataSet ->
            for (i in 0 until dataSet.entryCount) {
                val entry = dataSet.getEntryForIndex(i)
                if (entry.data == index) {
                    return entry
                }
            }
        }
        return null
    }

    private fun highlightIndex(selectedIndex: Int?, animated: Boolean) {
        if (selectedIndex == null || previousHighlightedIndex == selectedIndex) return
        logger.info("Graph: index=$selectedIndex animated=$animated")
        val entry = entryForIndex(selectedIndex)
        if (entry == null) {
            highlightValue(null, false)
            return
        }
        highlightValue(entry.x, entry.y, 0, false)
        moveToX(entry.x, animated)
        previousHighlightedIndex = selectedIndex
    }

    private fun moveToX(xValue: Float, animated: Boolean) {
        val xPadding = with(density) { 16.dp.toPx() }
        val xTarget: Float = xValue - visibleXRange * xPadding / 100
        if (animated) {
            moveViewToAnimated(xTarget, 0f, YAxis.AxisDependency.LEFT, 500)
        } else {
            moveViewToX(xTarget)
        }
    }

    private fun setupHorizontalEdges() {
        setViewPortOffsets(0f, 0f, 0f, 0f)
        val dragOffsetX = with(density) { Size.Graph.DRAG_OFFSET_X.toPx() }
        setDragOffsetX(dragOffsetX)
    }
}
