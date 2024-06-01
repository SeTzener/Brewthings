package com.brewthings.app.ui.android.chart.datasets

import com.brewthings.app.data.model.graph.GraphDataPoint
import com.brewthings.app.ui.android.chart.asChartXValue
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.Instant

/**
 * A base abstract class for all sensor value graph [data sets][LineDataSet].
 *
 * This class handles mapping of [GraphDataPoint] to [Entry].
 * The [Entry] 'x' value represents seconds since epoch.
 * The [Entry] 'y' value is defined by sub classes of this class.
 * The [Entry] 'data' value is null per default but can also be defined by a sub class of this class.
 *
 * @param sensorValues The list of [values][GraphDataPoint] that the data set should present.
 */
abstract class SensorValuesDataSet(
    sensorValues: List<GraphDataPoint>,
    asEntryYValue: GraphDataPoint.() -> Float
) : LineDataSet(mutableListOf(), "") {

    init {
        values = sensorValues.map {
            Entry(
                it.asChartXValue(),
                it.asEntryYValue(),
                it.asEntryDataValue()
            )
        }
    }

    /**
     * The time range of this data set, from the oldest to the latest [GraphDataPoint].
     * Returns null if the data set does not hold references to [GraphDataPoint] or if the data set is empty.
     */
    val timeRange: ClosedRange<Instant>? by lazy {
        when {
            !isHighlightEnabled || entryCount == 0 -> null
            else -> {
                val firstEntry = getSensorValue(entryIndex = 0)
                val lastEntry = getSensorValue(entryIndex = entryCount - 1)
                firstEntry.timestamp..lastEntry.timestamp
            }
        }
    }

    private fun GraphDataPoint.asEntryDataValue(): Any? =
        if (isHighlightEnabled) this else null

    private fun getSensorValue(entryIndex: Int): GraphDataPoint =
        getEntryForIndex(entryIndex)?.data as GraphDataPoint
}
