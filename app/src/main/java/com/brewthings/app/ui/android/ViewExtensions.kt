package com.brewthings.app.ui.android

import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner

/**
 * Runs the given [block] function once when the view tree is about to be drawn.
 */
fun View.runOnceOnPreDraw(block: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            block()
            // The OnPreDrawListener should run only once, so now that it ran, we remove it.
            viewTreeObserver.removeOnPreDrawListener(this)

            return true
        }
    })
}

fun View.lifecycleCoroutineScope(): LifecycleCoroutineScope? = findViewTreeLifecycleOwner()?.lifecycle?.coroutineScope
