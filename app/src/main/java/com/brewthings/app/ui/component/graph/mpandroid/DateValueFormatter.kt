package com.brewthings.app.ui.component.graph.mpandroid

import com.brewthings.app.util.datetime.roundMsToHour
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * A [ValueFormatter] that formats dates with a given date format [String].
 *
 * Note: The formatted value is rounded using [roundMsToHour] to work around a floating point accuracy issue, as
 * explained in [SensorValuesChart].
 */
class DateValueFormatter(dateFormat: String) : ValueFormatter() {

    private val formatter = SimpleDateFormat(dateFormat, Locale.US)

    override fun getFormattedValue(value: Float): String =
        formatter.format(Date(TimeUnit.SECONDS.toMillis(value.toLong()).roundMsToHour()))
}
