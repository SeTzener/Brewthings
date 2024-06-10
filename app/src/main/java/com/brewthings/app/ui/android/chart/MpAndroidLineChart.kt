package com.brewthings.app.ui.android.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import com.brewthings.app.ui.theme.Grey_Nevada
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis

@SuppressLint("ViewConstructor")
class MpAndroidLineChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val density: Density,
    chartData: ChartData?,
) : LineChart(context, attrs, defStyleAttr) {
    init {
        chartData?.also { updateDatasets(chartData) }

        configureXAxis()

        axisRight.isEnabled = false
        axisLeft.isEnabled = false

        description.isEnabled = false

        mRenderer.paintRender.strokeCap = Paint.Cap.ROUND

        legend.isEnabled = false
    }

    fun showData(chartData: ChartData) {
        updateDatasets(chartData)
        data.notifyDataChanged()
        notifyDataSetChanged()
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
            position = XAxis.XAxisPosition.BOTTOM
        }
    }
}
