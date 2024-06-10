package com.brewthings.app.ui.android.chart

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * This class allows drawing X axis labels with a rectangular background with rounded corners.
 *
 * @param gridLinePadding Padding between the the grid line and vertical edges of the chart.
 */
class XAxisLabelRenderer(
    private val gridLinePadding: Float,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {
    /**
     * This function is an adapted version of [XAxisRenderer.drawGridLine] to fit the desired design.
     * The modifications that have been implemented are:
     *
     * - Delegate directly to [XAxisRenderer.drawGridLine] if any of the nullable arguments of the function are null.
     * - Use [gridLinePadding] value to add padding between the chart edges and the grid line.
     */
    override fun drawGridLine(c: Canvas?, x: Float, y: Float, gridLinePath: Path?) {
        if (c == null || gridLinePath == null) {
            super.drawGridLine(c, x, y, gridLinePath)
            return
        }

        gridLinePath.moveTo(x, mViewPortHandler.contentBottom() - gridLinePadding)
        gridLinePath.lineTo(x, mViewPortHandler.contentTop() + gridLinePadding)

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint)

        gridLinePath.reset()
    }
}
