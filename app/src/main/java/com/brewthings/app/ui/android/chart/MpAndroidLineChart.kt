package com.brewthings.app.ui.android.chart

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis

@SuppressLint("ViewConstructor")
class MpAndroidLineChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    chartData: ChartData?,
) : LineChart(context, attrs, defStyleAttr) {
    init {
        chartData?.also { updateDatasets(chartData) }
        xAxis.valueFormatter = DateValueFormatter(dateFormat = "d/M")
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        axisRight.isEnabled = false
        axisLeft.isEnabled = false
        description.isEnabled = false
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
}
