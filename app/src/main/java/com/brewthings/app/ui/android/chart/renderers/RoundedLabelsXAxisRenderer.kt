package com.brewthings.app.ui.android.chart.renderers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.FontMetrics
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.ColorInt
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils.getSizeOfRotatedRectangleByDegrees
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * This class allows drawing X axis labels with a rectangular background with rounded corners.
 *
 * @param cornerRadius Corner radius of all corners of the rectangular background.
 * @param labelVerticalPadding Vertical padding between the label and the background left / right edges.
 * @param labelHorizontalPadding Horizontal padding between the label and the background top / bottom edges.
 * @param gridLinePadding Padding between the the grid line and vertical edges of the chart.
 * @param backgroundColor Color used for the label background.
 */
class RoundedLabelsXAxisRenderer(
    private val cornerRadius: Float,
    private val labelVerticalPadding: Float,
    private val labelHorizontalPadding: Float,
    private val gridLinePadding: Float,
    @ColorInt private val backgroundColor: Int,
    viewPortHandler: ViewPortHandler?,
    xAxis: XAxis?,
    transformer: Transformer?,
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    private val drawTextRectBuffer: Rect = Rect()
    private val fontMetricsBuffer: FontMetrics = FontMetrics()
    private val labelBackgroundPaint: Paint = Paint().apply {
        color = backgroundColor
    }

    /**
     * This function is an adapted version of [XAxisRenderer.drawLabel].
     * The modifications that have been implemented are:
     *
     * - Delegate directly to [XAxisRenderer.drawLabel] if any of the nullable arguments of the function are null.
     * - Call [drawRoundedBackground] before calling [Canvas.drawText].
     */
    @Suppress("MagicNumber") // Magic numbers were kept to keep the function similar to the original version.
    override fun drawLabel(
        canvas: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        if (formattedLabel == null || canvas == null || anchor == null) {
            super.drawLabel(canvas, formattedLabel, x, y, anchor, angleDegrees)
            return
        }

        var drawOffsetX = 0f
        var drawOffsetY = 0f
        val lineHeight = mAxisLabelPaint.getFontMetrics(fontMetricsBuffer)
        mAxisLabelPaint.getTextBounds(formattedLabel, 0, formattedLabel.length, drawTextRectBuffer)
        // Android sometimes has pre-padding
        drawOffsetX -= drawTextRectBuffer.left.toFloat()
        // Android does not snap the bounds to line boundaries,
        //  and draws from bottom to top.
        // And we want to normalize it.
        drawOffsetY += -fontMetricsBuffer.ascent
        // To have a consistent point of reference, we always draw left-aligned
        val originalTextAlign = mAxisLabelPaint.textAlign
        mAxisLabelPaint.textAlign = Align.LEFT
        if (angleDegrees != 0f) {
            // Move the text drawing rect in a way that it always rotates around its center
            drawOffsetX -= drawTextRectBuffer.width() * 0.5f
            drawOffsetY -= lineHeight * 0.5f
            var translateX = x
            var translateY = y

            // Move the "outer" rect relative to the anchor, assuming its centered
            if (anchor.x != 0.5f || anchor.y != 0.5f) {
                val rotatedSize = getSizeOfRotatedRectangleByDegrees(
                    drawTextRectBuffer.width().toFloat(),
                    lineHeight,
                    angleDegrees
                )
                translateX -= rotatedSize.width * (anchor.x - 0.5f)
                translateY -= rotatedSize.height * (anchor.y - 0.5f)
                FSize.recycleInstance(rotatedSize)
            }
            canvas.save()
            canvas.translate(translateX, translateY)
            canvas.rotate(angleDegrees)
            canvas.drawRoundedBackground(x = drawOffsetX, y = drawOffsetY)
            canvas.drawText(formattedLabel, drawOffsetX, drawOffsetY, mAxisLabelPaint)
            canvas.restore()
        } else {
            if (anchor.x != 0f || anchor.y != 0f) {
                drawOffsetX -= drawTextRectBuffer.width() * anchor.x
                drawOffsetY -= lineHeight * anchor.y
            }
            drawOffsetX += x
            drawOffsetY += y

            canvas.drawRoundedBackground(x = drawOffsetX, y = drawOffsetY)
            canvas.drawText(formattedLabel, drawOffsetX, drawOffsetY, mAxisLabelPaint)
        }
        mAxisLabelPaint.textAlign = originalTextAlign
    }

    private fun Canvas.drawRoundedBackground(x: Float, y: Float) {
        val rect = RectF(
            x - labelHorizontalPadding,
            y - drawTextRectBuffer.height() - labelVerticalPadding,
            x + drawTextRectBuffer.width() + labelHorizontalPadding,
            y + labelVerticalPadding
        )
        drawRoundRect(rect, cornerRadius, cornerRadius, labelBackgroundPaint)
    }

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
