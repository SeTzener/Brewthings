package com.brewthings.app.ui.android.chart

import android.view.View
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * A [ViewPortHandler] that calls an [onCenterViewPort] callback function whenever [ViewPortHandler.centerViewPort] is
 * called.
 *
 * @param onCenterViewPort Called when [ViewPortHandler.centerViewPort] is called.
 */
class ViewPortHandlerWithCallback(private val onCenterViewPort: () -> Unit) : ViewPortHandler() {

    override fun centerViewPort(transformedPts: FloatArray?, view: View?) {
        super.centerViewPort(transformedPts, view)
        onCenterViewPort()
    }
}
