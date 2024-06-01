package com.brewthings.app.ui.android.chart.datasets

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.Instant

/**
 *  An invisible [data set][LineDataSet] that is used to show parts of the chart that have no values. This allows
 *  showing the graph while loading data.
 *
 *  The timeline start and end are defined by the a [ClosedRange] of [Instant] that is passed to the constructor. At the
 *  two extremes of the this data set are the points in time defined by the provided time range.
 *
 *  Between the timeline start and end point, the data set can contain data that replicates other visible data sets in
 *  the graph. This is achieved using the [updateValues] function and is done to avoid having this data set influence
 *  the auto scaling of the graph.
 *
 *  @param timeRange The entire time range that the graph will show.
 */
class TimelineDataSet(private var timeRange: ClosedRange<Instant>) : InvisibleDataSet(entries = timeRange.asEntries()) {

    fun updateValues(
        timeRange: ClosedRange<Instant>,
        visibleValues: List<Entry>,
    ) {
        val first = values.first()
        val last = values.last()

        // Update first and last entries x values, if necessary.
        if (this.timeRange != timeRange) {
            this.timeRange = timeRange
            first.x = timeRange.start.epochSecond.toFloat()
            last.x = timeRange.endInclusive.epochSecond.toFloat()
        }

        // Update first and last entries y values to match the visible data
        first.y = visibleValues.firstOrNull()?.y ?: defaultYValue
        last.y = visibleValues.lastOrNull()?.y ?: defaultYValue

        // update the data set entries
        values.clear()
        values.add(first)
        values.addAll(1, visibleValues)
        values.add(last)

        notifyDataSetChanged()
    }

    companion object {

        private const val defaultYValue = 0f

        private fun ClosedRange<Instant>.asEntries(): MutableList<Entry> = mutableListOf(
            Entry(start.epochSecond.toFloat(), defaultYValue),
            Entry(endInclusive.epochSecond.toFloat(), defaultYValue)
        )
    }
}
