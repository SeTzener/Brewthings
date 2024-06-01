package com.brewthings.app.ui.android.chart

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.jobs.AnimatedMoveViewJob
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.Transformer

/**
 * An [AnimatedMoveViewJob] that can be [cancelled][cancel].
 */
class CancelableAnimatedMoveViewJob private constructor(
    lineChart: LineChart,
    xTarget: Float,
    yTarget: Float,
    transformer: Transformer = lineChart.getTransformer(YAxis.AxisDependency.RIGHT),
    xOrigin: Float,
    yOrigin: Float,
    durationMs: Long,
) : AnimatedMoveViewJob(
    lineChart.viewPortHandler,
    xTarget,
    yTarget,
    transformer,
    lineChart,
    xOrigin,
    yOrigin,
    durationMs,
) {

    fun cancel() {
        animator.cancel()
    }

    companion object {

        /**
         * Adapted from [com.github.mikephil.charting.charts.BarLineChartBase.moveViewToAnimated].
         */
        fun create(
            lineChart: LineChart,
            xTarget: Float,
            yTarget: Float = 0f,
            axis: YAxis.AxisDependency = YAxis.AxisDependency.RIGHT,
            durationMs: Long,
        ): CancelableAnimatedMoveViewJob {
            val viewPortHandler = lineChart.viewPortHandler
            val yInView: Float = lineChart.getAxis(axis).mAxisRange / viewPortHandler.scaleY
            val bounds =
                lineChart.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentTop(), axis)

            return CancelableAnimatedMoveViewJob(
                lineChart = lineChart,
                xTarget = xTarget,
                yTarget = yTarget + yInView / 2,
                xOrigin = bounds.x.toFloat(),
                yOrigin = bounds.y.toFloat(),
                durationMs = durationMs,
            ).also {
                MPPointD.recycleInstance(bounds)
            }
        }
    }
}
