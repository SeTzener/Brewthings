package com.brewthings.app.ui.android.chart.renderers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.ColorInt
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * This class implements customised Y axis rendering, specifically:
 * - Draws Y axis labels with a rounded rectangular background.
 * - Implements custom drawing for limit lines.
 *
 * @param cornerRadius Corner radius of all corners of the rectangular background.
 * @param labelVerticalPadding Vertical padding between the label and the background left / right edges.
 * @param labelHorizontalPadding Horizontal padding between the label and the background top / bottom edges.
 * @param yLabelBackgroundColor Color used for the y label background.
 */
class SensorGraphYAxisRenderer(
    private val cornerRadius: Float,
    private val labelVerticalPadding: Float,
    private val labelHorizontalPadding: Float,
    @ColorInt private val yLabelBackgroundColor: Int,
    viewPortHandler: ViewPortHandler?,
    yAxis: YAxis,
    transformer: Transformer?,
) : YAxisRenderer(viewPortHandler, yAxis, transformer) {

    private val drawTextRectBuffer: Rect = Rect()
    private val yLabelBackgroundPaint: Paint = Paint().apply {
        color = yLabelBackgroundColor
    }

    /**
     * This function is an adapted version of [YAxisRenderer.drawYLabels].
     * The modifications that have been implemented are:
     *
     * - Delegate directly to [YAxisRenderer.drawYLabels] if any of the nullable arguments of the function are null.
     * - Call [drawRoundedBackground] before calling [Canvas.drawText].
     */
    @SuppressWarnings("VariableMinLength") // The short variable names in this function are fine.
    override fun drawYLabels(canvas: Canvas?, fixedPosition: Float, positions: FloatArray?, offset: Float) {
        if (canvas == null || positions == null) {
            super.drawYLabels(canvas, fixedPosition, positions, offset)
            return
        }

        val from = if (mYAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        val to = if (mYAxis.isDrawTopYLabelEntryEnabled) mYAxis.mEntryCount else mYAxis.mEntryCount - 1

        // draw
        for (i in from until to) {
            val text = mYAxis.getFormattedLabel(i)
            mAxisLabelPaint.getTextBounds(text, 0, text.length, drawTextRectBuffer)

            val y = positions[i * 2 + 1] + offset
            canvas.drawRoundedBackground(x = fixedPosition, y = y)
            canvas.drawText(text, fixedPosition, y, mAxisLabelPaint)
        }
    }

    private fun Canvas.drawRoundedBackground(x: Float, y: Float) {
        val rect = RectF(
            x - drawTextRectBuffer.width() - labelHorizontalPadding,
            y - drawTextRectBuffer.height() - labelVerticalPadding,
            x + labelHorizontalPadding,
            y + labelVerticalPadding
        )
        drawRoundRect(rect, cornerRadius, cornerRadius, yLabelBackgroundPaint)
    }
}
