package com.brewthings.app.ui.android.chart

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart

@SuppressLint("ViewConstructor")
class MpAndroidLineChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    chartData: ChartData?,
) : LineChart(context, attrs, defStyleAttr) {
    init {
        chartData?.also { updateDatasets(chartData) }
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
