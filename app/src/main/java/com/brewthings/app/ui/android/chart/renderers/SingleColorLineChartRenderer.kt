package com.brewthings.app.ui.android.chart.renderers

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * A [LineChartRenderer] that will color the line with a single, provided color.
 */
class SingleColorLineChartRenderer(
    lineColor: Int, // TODO(walt): change per data type.
    chart: LineDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?,
) : LineChartRenderer(chart, animator, viewPortHandler) {
    private val circlesBuffer = FloatArray(2)
    private val circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = lineColor
    }

    init {
        mRenderPaint.color = lineColor
    }

    override fun drawCircles(c: Canvas) {
        mRenderPaint.style = Paint.Style.FILL

        val phaseY = mAnimator.phaseY

        circlesBuffer[0] = 0f
        circlesBuffer[1] = 0f

        val dataSets = mChart.lineData.dataSets

        for (i in dataSets.indices) {
            val dataSet = dataSets[i]

            if (!dataSet.isVisible || !dataSet.isDrawCirclesEnabled || dataSet.entryCount == 0) continue

            mCirclePaintInner.color = dataSet.circleHoleColor

            val trans = mChart.getTransformer(dataSet.axisDependency)

            mXBounds[mChart] = dataSet

            val circleRadius = dataSet.circleRadius

            val boundsRangeCount = mXBounds.range + mXBounds.min

            for (j in mXBounds.min..boundsRangeCount) {
                val entry = dataSet.getEntryForIndex(j) ?: break

                circlesBuffer[0] = entry.x
                circlesBuffer[1] = entry.y * phaseY

                trans.pointValuesToPixel(circlesBuffer)

                if (!mViewPortHandler.isInBoundsRight(circlesBuffer[0])) break
                if (!mViewPortHandler.isInBoundsLeft(circlesBuffer[0]) ||
                    !mViewPortHandler.isInBoundsY(circlesBuffer[1])
                ) {
                    continue
                }

                // For simplicity, this renderer does not support neither circle holes, nor buffered bitmaps.
                c.drawCircle(
                    circlesBuffer[0],
                    circlesBuffer[1],
                    circleRadius,
                    circlePaint
                )
            }
        }
    }
}
