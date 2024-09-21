package com.brewthings.app.ui.screens.pill.graph

import com.brewthings.app.data.model.RaptPillData

fun List<RaptPillData>.toGraphState(): GraphState {
    val series = listOf(
        GraphSeries(
            type = DataType.TEMPERATURE,
            data = mapIndexed { index, data -> data.toDataPoint(index) { temperature } }
        ),
        GraphSeries(
            type = DataType.GRAVITY,
            data =mapIndexed { index, data -> data.toDataPoint(index) { gravity } }
        ),
        GraphSeries(
            type = DataType.BATTERY,
            data = mapIndexed { index, data -> data.toDataPoint(index) { battery } }
        )
    )
    return GraphState(series)
}

private fun RaptPillData.toDataPoint(index: Int, toY: RaptPillData.() -> Float): DataPoint = DataPoint(
    x = timestamp.epochSeconds.toFloat(),
    y = toY(),
    isOG = isOG,
    isFG = isFG,
    data = index
)
