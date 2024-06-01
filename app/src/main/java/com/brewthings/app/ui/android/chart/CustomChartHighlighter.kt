package com.brewthings.app.ui.android.chart

import com.github.mikephil.charting.highlight.ChartHighlighter
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider
import kotlin.math.abs

/**
 * Custom implementation of [ChartHighlighter].
 */
class CustomChartHighlighter<T : BarLineScatterCandleBubbleDataProvider>(chart: T) : ChartHighlighter<T>(chart) {
    /**
     * Custom implementation of `getDistance`.
     * ChartHighlighter implements this method in the following way:
     * ```
     * hypot(x1 - x2, y1 - y2)
     * ```
     * This is problematic because if you have several nearby points, the highlighter tries to find the
     * closest hypotenuse by using pythagoras. This takes into account the y value, but this can make
     * the marker jump to unexpected entries. Instead we provide a custom implementation where we
     * return the difference between the x values. We only ever have one entry per x value, so this
     * always gets the nearest entry.
     */
    override fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float = abs(x1 - x2)
}
