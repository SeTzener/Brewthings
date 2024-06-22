package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillData

fun List<RaptPillData>.toGraphData(): GraphData {
    val series = listOf(
        GraphSeries(
            type = DataType.TEMPERATURE,
            data = map { it.toDataPoint { temperature } }
        ),
        GraphSeries(
            type = DataType.GRAVITY,
            data = map { it.toDataPoint { gravity } }
        ),
        GraphSeries(
            type = DataType.BATTERY,
            data = map { it.toDataPoint { battery } }
        )
    )
    return GraphData(series)
}

private fun RaptPillData.toDataPoint(toY: RaptPillData.() -> Float): DataPoint = DataPoint(
    x = timestamp.epochSeconds.toFloat(),
    y = toY(),
    data = this // RaptPillData is passed as Any?
)
