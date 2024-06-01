package com.brewthings.app.ui.android.chart

import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import androidx.compose.ui.unit.Density
import androidx.core.view.isVisible
import com.brewthings.app.data.model.graph.DeviceSensorGraphState
import com.brewthings.app.data.model.graph.GraphTheme
import com.brewthings.app.data.model.graph.GraphTimeSpan
import com.brewthings.app.data.model.graph.SegmentSensorValue
import com.brewthings.app.data.model.graph.SelectedGraphValue
import com.brewthings.app.data.model.graph.SensorValuesGraphEvent
import com.brewthings.app.ui.android.chart.datasets.LineChartDataSet
import com.brewthings.app.ui.android.chart.datasets.SensorValuesDataSet
import com.brewthings.app.ui.android.chart.datasets.TimelineDataSet
import com.brewthings.app.ui.android.chart.renderers.SingleColorLineChartRenderer
import com.brewthings.app.ui.android.isWithin
import com.brewthings.app.ui.android.lifecycleCoroutineScope
import com.brewthings.app.ui.android.runOnceOnPreDraw
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.BarLineChartTouchListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.time.Instant
import java.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val DECELERATION_COEFFICIENT = 0.8f

/**
 * This class implements a [LineChart] that is specific for showing [SegmentSensorValues]. This is achieved by
 * implementing different types of [data sets][ILineDataSet]. See docs/graphs.md for more information.
 *
 * Updating the chart data is done by calling [showData].
 *
 * Note:
 * MPAndroidChart uses [Float] values for storing data. We convert [Instant.epochSeconds] to a [Float] for the graph 'x'
 * values. When converting the [Float] back to a [Long] some data is lost and the result is not the original
 * [Instant.epochSeconds] value. This is a [known issue][https://github.com/PhilJay/MPAndroidChart/issues/4854]. We work
 * around this issue by rounding the seconds value to the nearest hour using [roundMsToHour] and [roundSecToHour].
 */
@Suppress("MagicNumber", "ViewConstructor")
class SensorValuesChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val density: Density,
    private val theme: GraphTheme,
    private val surfaceColor: Int,
    private val primaryColor: Int,
    private val secondaryColor: Int,
    private var graphState: DeviceSensorGraphState,
    private var graphTimeSpan: GraphTimeSpan,
    events: Flow<SensorValuesGraphEvent>,
    private val onVisibleRangeChanged: (ClosedRange<Instant>) -> Unit,
    private val onSelectedValueChanged: (SelectedGraphValue?) -> Unit,
) : LineChart(context, attrs, defStyleAttr) {
    private val timelineDataSet = TimelineDataSet(timeRange = graphState.graphTimeRange)

    private val sensorValueSelector = object : OnChartValueSelectedListener {
        override fun onValueSelected(entry: Entry, highlight: Highlight) {
            val sensorValue = entry.data as SegmentSensorValue
            animateToCenter(sensorValue.timestamp)
        }

        override fun onNothingSelected() {
            // do nothing.
        }
    }

    private var centerGraphJob: CancelableAnimatedMoveViewJob? = null

    // We postpone handling some events until onDataUpdated() is called, to avoid handling them too early.
    private var toHandleOnDataUpdated: SensorValuesGraphEvent? = SensorValuesGraphEvent.MoveToEnd

    private var selectedSensorValue: SegmentSensorValue? = null

    init {
        observe(events)
        initViewPortHandler()
        isVisible = false
        legend.isEnabled = false
        setPinchZoom(false)
        isDoubleTapToZoomEnabled = false
        isScaleXEnabled = false
        isScaleYEnabled = false
        isAutoScaleMinMaxEnabled = true
        minOffset = 0f
        // Prevents x-axis labels from being clipped.
        extraBottomOffset = with(density) { Size.Graph.BOTTOM_SPACING.toPx() }
        extraTopOffset = 0f
        setDrawMarkers(false)
        description.isEnabled = false

        configureAxisRenderers()
        configureXAxis()
        configureRightAxis()
        axisLeft.isEnabled = false
        mRenderer = SingleColorLineChartRenderer(
            lineColor = primaryColor,
            chart = this,
            animator = mAnimator,
            viewPortHandler = mViewPortHandler,
        )
        mRenderer.paintRender.strokeCap = Paint.Cap.ROUND

        onChartGestureListener = VisibleRangeUpdatedListener(
            onChartGestureStart = { centerGraphJob?.cancel() },
            onChartTranslate = ::onVisibleRangeUpdated,
        )
        setOnChartValueSelectedListener(sensorValueSelector)
        dragDecelerationFrictionCoef = DECELERATION_COEFFICIENT
        setHighlighter(CustomChartHighlighter(this))
        onDataUpdated()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (width != oldWidth) {
            val widthDp = with(density) { width.toDp() }
            setDragOffsetX(widthDp.value / 2)
        }
    }

    private fun observe(events: Flow<SensorValuesGraphEvent>) {
        // Wait for the view to draw so that it has a LifecycleOwner, before trying to collect the event Flow.
        runOnceOnPreDraw {
            lifecycleCoroutineScope()?.launch {
                events.collect { event ->
                    when (event) {
                        is SensorValuesGraphEvent.MoveToEnd,
                        is SensorValuesGraphEvent.CenterOn -> toHandleOnDataUpdated = event
                        is SensorValuesGraphEvent.TimeSpanChanged,
                        is SensorValuesGraphEvent.CenterAnimatedOn,
                        is SensorValuesGraphEvent.ClearSelectedValue -> handle(event)
                    }
                }
            }
        }
    }

    private fun initViewPortHandler() {
        mViewPortHandler = ViewPortHandlerWithCallback {
            isVisible = true
            updateSelectedValue()
        }
        // 'init()' call is required after changing the mViewPortHandler.
        init()
    }

    private fun handle(event: SensorValuesGraphEvent) {
        when (event) {
            is SensorValuesGraphEvent.TimeSpanChanged -> onTimeSpanChanged()
            is SensorValuesGraphEvent.MoveToEnd -> moveToEnd()
            is SensorValuesGraphEvent.CenterOn -> centerGraphOn(instant = event.instant)
            is SensorValuesGraphEvent.CenterAnimatedOn -> animateToEnd(instant = event.instant)
            is SensorValuesGraphEvent.ClearSelectedValue -> sensorValueSelector.onNothingSelected()
        }
    }

    fun showData(
        graphState: DeviceSensorGraphState,
        graphTimeSpan: GraphTimeSpan
    ) {
        val oldScreenState = this.graphState
        if (oldScreenState == graphState) return
        centerGraphJob?.cancel()
        this.graphState = graphState
        this.graphTimeSpan = graphTimeSpan
        onDataUpdated()

        when {
            oldScreenState.sensorValues != graphState.sensorValues -> {
                // the sensor values have change by x-scrolling the graph or changing sensor page
                onSensorValuesChanged()
            }
        }
    }

    private fun onDataUpdated() {
        isDragXEnabled = graphState.sensorValues != null
        xAxis.valueFormatter = DateValueFormatter(dateFormat = graphState.dateFormat)
        xAxis.granularity = graphTimeSpan.asXAxisGranularity().toFloat()

        updateDataSets()
        updateAxisRight()
        setVisibleXRange(graphState.visibleTimePeriod.toFloat(), graphState.visibleTimePeriod.toFloat())

        toHandleOnDataUpdated?.also { handle(it) }
        toHandleOnDataUpdated = null
    }

    private fun onShowThresholdsChanged() {
        animateToSelectedSensorValue()
    }

    private fun onShowMinMaxChanged() {
        animateToSelectedSensorValue()
    }

    private fun animateToSelectedSensorValue() {
        selectedSensorValue?.also {
            // Animating to the selected value seems to fix an issue where we have either toggled the threshold or the
            // min/max value, and the graph marker needs to be vertically adjusted
            animateToCenter(it.timestamp)
        }
    }

    private fun onSensorValuesChanged() {
        // do nothing.
    }

    private fun updateAxisRight() {
        with(axisRight) {
            limitLines.clear()
            isEnabled = graphState.sensorValues?.values?.isNotEmpty() == true
        }
    }

    private fun onTimeSpanChanged() {
        stopDeceleration()
    }

    private fun moveToEnd() {
        moveViewToX(Float.MAX_VALUE)
    }

    private fun centerGraphOn(instant: Instant) {
        lifecycleCoroutineScope()?.launch {
            centerViewTo(instant.epochSecond.toFloat(), 0f, YAxis.AxisDependency.RIGHT)
        }
    }

    private fun animateToEnd(instant: Instant) =
        animateTo(instant.epochSecond - graphState.visibleTimePeriod)

    private fun animateToCenter(instant: Instant) =
        animateTo(instant.epochSecond - graphState.visibleTimePeriod / 2)

    private fun animateTo(xTarget: Long) {
        centerGraphJob?.cancel()
        stopDeceleration()
        centerGraphJob = CancelableAnimatedMoveViewJob.create(
            lineChart = this,
            xTarget = xTarget.toFloat(),
            durationMs = ANIMATED_MOVE_DURATION_MS,
        ).apply {
            run()
        }
    }

    private fun updateDataSets() {
        data = LineData(timelineDataSet)
        timelineDataSet.updateValues(
            timeRange = graphState.graphTimeRange,
            visibleValues = graphState.sensorValues?.values?.map {
                Entry(it.asChartXValue(), it.maxValue)
            } ?: emptyList()
        )
        updateVisibleDataSets()
        data.notifyDataChanged()
        notifyDataSetChanged()
        invalidate()
    }

    private fun updateVisibleDataSets() {
        graphState.sensorValues?.values?.splitByGaps(minGapDuration = graphState.maxValueTimeGap)
            ?.forEach { valueGroup ->
                // Gravity chart
                data.addDataSet(
                    LineChartDataSet(
                        sensorValues = valueGroup,
                        highlightEnabled = true,
                        lineColor = primaryColor,
                        asEntryYValue = { gravity },
                    )
                )

                // Temperature chart
                data.addDataSet(
                    LineChartDataSet(
                        sensorValues = valueGroup,
                        highlightEnabled = true,
                        lineColor = secondaryColor,
                        asEntryYValue = { temperature },
                    )
                )
            }
    }

    private fun onChartVisibleRangeUpdate() {
        val visibleRange = getVisibleRangeWithWorkaround()
        val start = Instant.fromEpochSeconds(visibleRange.start.toLong().roundSecToHour())
        val end = Instant.fromEpochSeconds(visibleRange.endInclusive.toLong().roundSecToHour())
        onVisibleRangeChanged(start..end)
    }

    private fun onVisibleRangeUpdated(dragOffset: MPPointF) {
        onChartVisibleRangeUpdate()
        updateSelectedValue(dragOffset)
    }

    private fun updateSelectedValue(dragOffset: MPPointF? = null) {
        val sensorUnit = graphState.sensorValues?.unit
        val entry = getEntryByTouchPoint(center.x, center.y)

        if (sensorUnit != null && entry != null) {
            val transformer = getTransformer(YAxis.AxisDependency.RIGHT)
            val sensorValue = entry.data as SegmentSensorValue
            val pixel = transformer.getPixelForValues(entry.x, entry.y)

            // while dragging, getPixelForValues returns the dY of the motion event;  we don't need it since our graph
            // has a fixed height, so we have to subtract it from the result.
            val yPos = pixel.y.toFloat() - (dragOffset?.y ?: 0f)

            // same problem but the graph is panning, so we need to coerce the x to screen center only when it reaches
            // the first or the last value in the dataset.
            val isFirst = graphState.sensorValues?.values?.firstOrNull() == sensorValue
            val isLast = graphState.sensorValues?.values?.lastOrNull() == sensorValue
            val xPos = if (isFirst || isLast) width.toFloat() / 2 else pixel.x.toFloat()

            onSelectedValueChanged(
                SelectedGraphValue(
                    sensorUnit = sensorUnit,
                    sensorValue = sensorValue,
                    xPos = xPos,
                    yPos = yPos,
                )
            )
            selectedSensorValue = sensorValue
        }
    }

    /**
     * This function provides a workaround for [a known issue][https://github.com/PhilJay/MPAndroidChart/issues/4739] in
     * MPAndroidChart where 'LineChart.lowestVisibleX' and 'LineChart.highestVisibleX' sometimes provides the wrong
     * values.
     *
     * @param visibleRange The chart visible range.
     * @return The currently visible range of X values.
     */
    private fun getVisibleRangeWithWorkaround(
        visibleRange: Float = graphState.visibleTimePeriod.toFloat(),
    ): ClosedRange<Float> {
        var lowestVisibleX = lowestVisibleX
        var highestVisibleX = highestVisibleX

        if (lowestVisibleX <= xAxis.axisMinimum) {
            highestVisibleX = lowestVisibleX + visibleRange
        } else if (highestVisibleX >= xAxis.axisMaximum) {
            lowestVisibleX = highestVisibleX - visibleRange
        }

        return lowestVisibleX..highestVisibleX
    }

    private fun configureAxisRenderers() {
        val labelVerticalPadding = with(density) { Size.Graph.BOTTOM_SPACING.toPx() }
        val labelHorizontalPadding = with(density) { Size.Graph.Y_LABEL_HORIZONTAL_PADDING.toPx() }
        val labelBackgroundColorThemed = when (theme) {
            Theme.LIGHT -> AirthingsColors.LIGHT_GREY
            Theme.DARK -> AirthingsColors.SHARK
        }
        val labelBackgroundColor = labelBackgroundColorThemed.copy(alpha = 0.9f).toArgb()
        val labelCornerRadius = with(density) { Size.Graph.LABEL_CORNER_RADIUS.toPx() }
        setXAxisRenderer(
            RoundedLabelsXAxisRenderer(
                cornerRadius = labelCornerRadius,
                labelVerticalPadding = labelVerticalPadding,
                labelHorizontalPadding = labelHorizontalPadding,
                gridLinePadding = with(density) { Size.Graph.GRID_LINE_PADDING.toPx() },
                backgroundColor = labelBackgroundColor,
                viewPortHandler = viewPortHandler,
                xAxis = xAxis,
                transformer = getTransformer(YAxis.AxisDependency.LEFT),
            )
        )

        rendererRightYAxis = SensorGraphYAxisRenderer(
            cornerRadius = labelCornerRadius,
            labelVerticalPadding = labelVerticalPadding,
            labelHorizontalPadding = labelHorizontalPadding,
            yLabelBackgroundColor = labelBackgroundColor,
            viewPortHandler = viewPortHandler,
            yAxis = axisRight,
            transformer = getTransformer(YAxis.AxisDependency.RIGHT),
        )
    }

    private fun configureRightAxis() {
        with(axisRight) {
            setDrawGridLines(false)
            setDrawGridLinesBehindData(false)
            setDrawAxisLine(false)
            textColor = AirthingsColors.CONGRUENCE.toArgb()
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            labelCount = LABEL_COUNT
            xOffset = with(density) { Size.Graph.RIGHT_AXIS_PADDING.toPx() }
        }
    }

    private fun configureXAxis() {
        with(xAxis) {
            setDrawGridLines(true)
            setDrawGridLinesBehindData(false)
            val dashedLineLength = with(density) { Size.Graph.DASHED_LINE_LENGTH.toPx() }
            setGridDashedLine(DashPathEffect(floatArrayOf(dashedLineLength, dashedLineLength), 0f))
            gridColor = AirthingsColors.GREY_NEVADA.toArgb()
            setDrawAxisLine(false)
            position = XAxis.XAxisPosition.BOTTOM
            textColor = AirthingsColors.CONGRUENCE.toArgb()
            granularity = TimeUnit.HOURS.toSeconds(X_AXIS_GRANULARITY_HOURS).toFloat()
        }
    }

    private fun stopDeceleration() {
        (onTouchListener as BarLineChartTouchListener).stopDeceleration()
    }

    override fun autoScale() {
        super.autoScale()
        // Call notifyDataSetChanged() to force scaling not only of the y Axis, but also of the data.
        notifyDataSetChanged()
    }

    private fun Threshold.asLimitLine(): LimitLine {
        val limit = value.toFloat()
        val label = axisRight.valueFormatter.getFormattedValue(limit)

        val color = DeviceSensorsColor.forThreshold(
            sensorType = sensorType,
            thresholdLevel = thresholdLevel,
        ).toArgb()

        return LimitLine(limit, label).apply {
            lineColor = color
            lineWidth = Size.Graph.DASHED_LINE_WIDTH.value
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            yOffset = with(density) { Size.Graph.LIMIT_LABEL_VERTICAL_PADDING.toPx() }
            xOffset = with(density) { Size.Graph.LIMIT_LABEL_HORIZONTAL_PADDING.toPx() }
            textColor = color
        }
    }

    companion object {
        private const val ANIMATED_MOVE_DURATION_MS = 1000L
        private const val LABEL_COUNT = 4
        private const val X_AXIS_GRANULARITY_HOURS = 4L
    }
}

fun SegmentSensorValue.asChartXValue(): Float = timestamp.epochSecond.toFloat()

@Suppress("MagicNumber")
private fun GraphTimeSpan.asXAxisGranularity(): Double {
    val hours = 60.0 * 60.0

    return when (this) {
        GraphTimeSpan.THREE_HOURS -> hours
    }
}

private fun SensorValues.getMaxSensorValue(): Float = data

/**
 * Splits a list of [SegmentSensorValue] into multiple lists of [SegmentSensorValue]. Each list in the result is a group
 * of values without gaps between them. The time duration of a gap is defined by [minGapDuration]. Consecutive values
 * with a time gap greater than [minGapDuration] will be returned in the result in separate lists.
 *
 * This function assumes that the [values][SegmentSensorValue] are sorted chronologically.
 *
 * @param minGapDuration The minimal amount of time that defines a gap between values. If two consecutive values have a
 * time difference between them that is greater than [minGapDuration] then the function considers that time between
 * those two values as a data gap.
 */
private fun List<SegmentSensorValue>.splitByGaps(minGapDuration: Duration): List<List<SegmentSensorValue>> {
    val valueGroups = mutableListOf<List<SegmentSensorValue>>()

    var lastValueTimestamp: Instant? = null
    var currentGroup: MutableList<SegmentSensorValue> = mutableListOf()
    forEach { value ->
        val isNewGroup = lastValueTimestamp?.let { !value.timestamp.isWithin(it, minGapDuration) } ?: true
        if (isNewGroup) {
            currentGroup = mutableListOf()
            valueGroups.add(currentGroup)
        }
        currentGroup.add(value)
        lastValueTimestamp = value.timestamp
    }

    return valueGroups
}
