package com.brewthings.app.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class Logger(private val tag: String) {
    fun info(message: String) {
        Log.i(tag, message)
        Firebase.crashlytics.log(composeFirebaseLog(tag, "INFO", message))
    }

    fun warning(message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
        Firebase.crashlytics.log(composeFirebaseLog(tag, "WARNING", message))
        throwable?.also { Firebase.crashlytics.recordException(it) }
    }

    fun error(message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        Firebase.crashlytics.log(composeFirebaseLog(tag, "ERROR", message))
        throwable?.also { Firebase.crashlytics.recordException(it) }
    }

    private fun composeFirebaseLog(tag: String, level: String, message: String): String = "[$tag] $level: $message"
}
