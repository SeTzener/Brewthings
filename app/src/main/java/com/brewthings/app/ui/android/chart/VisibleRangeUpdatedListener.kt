package com.brewthings.app.ui.android.chart

import android.view.MotionEvent
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF

/**
 * An [OnChartGestureListener] that calls provided callback functions.
 *
 * @param onChartGestureStart A function that is called when [onChartGestureStart] is called.
 * @param onChartTranslate A function that is called when [onChartTranslate] is called.
 */
class VisibleRangeUpdatedListener(
    private val onChartGestureStart: () -> Unit,
    private val onChartTranslate: (MPPointF) -> Unit,
) : OnChartGestureListener {

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        // Do nothing.
    }

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        MPPointF.getInstance(dX, dY).also {
            onChartTranslate(it)
            MPPointF.recycleInstance(it)
        }
    }

    override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
        onChartGestureStart()
    }

    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
        // Do nothing.
    }

    override fun onChartLongPressed(me: MotionEvent?) {
        // Do nothing.
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {
        // Do nothing.
    }

    override fun onChartSingleTapped(me: MotionEvent?) {
        // Do nothing.
    }

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
        // Do nothing.
    }
}
