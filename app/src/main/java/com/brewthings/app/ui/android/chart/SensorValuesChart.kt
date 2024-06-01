package com.brewthings.app.ui.android.chart

import android.content.Context
import android.util.AttributeSet
import androidx.compose.ui.unit.Density
import com.brewthings.app.data.model.graph.DeviceSensorGraphState
import com.brewthings.app.data.model.graph.GraphTheme
import com.brewthings.app.data.model.graph.GraphTimeSpan
import com.brewthings.app.data.model.graph.SelectedGraphValue
import com.brewthings.app.data.model.graph.SensorValuesGraphEvent
import com.github.mikephil.charting.charts.LineChart
import java.time.Instant
import kotlinx.coroutines.flow.Flow

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

    private val multicolorRenderer: MulticolorLineChartRenderer get() = mRenderer as MulticolorLineChartRenderer

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
        mRenderer = MulticolorLineChartRenderer(
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

        val sensorValues = graphState.sensorValues
        multicolorRenderer.colorThresholds = if (graphState.showColors && sensorValues != null) {
            sensorValues.asLineDataSetThresholds()
        } else {
            emptyList()
        }

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
            if (graphState.showThresholds) addThresholdLimitLines()
            isEnabled = graphState.sensorValues?.values?.isNotEmpty() == true
        }
    }

    private fun YAxis.addThresholdLimitLines() {
        graphState.sensorValues?.let { sensorValues ->
            sensorValues.thresholds
                .map { it.asLimitLine() }
                .forEach { addLimitLine(it) }
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
            centerViewTo(instant.epochSeconds.toFloat(), 0f, YAxis.AxisDependency.RIGHT)
        }
    }

    private fun animateToEnd(instant: Instant) =
        animateTo(instant.epochSeconds - graphState.visibleTimePeriod)

    private fun animateToCenter(instant: Instant) =
        animateTo(instant.epochSeconds - graphState.visibleTimePeriod / 2)

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
                Entry(it.asChartXValue(), it.value.toFloat())
            } ?: emptyList()
        )
        if (graphState.showThresholds) {
            graphState.sensorValues?.thresholds?.forEach { threshold ->
                data.addDataSet(
                    InvisibleDataSet(
                        xValues = graphState.sensorValues?.values?.map { it.asChartXValue() } ?: emptyList(),
                        yValue = threshold.value.toFloat(),
                    )
                )
            }
        }
        updateVisibleDataSets()
        data.notifyDataChanged()
        notifyDataSetChanged()
        invalidate()
    }

    private fun updateVisibleDataSets() {
        graphState.sensorValues?.values?.splitByGaps(minGapDuration = graphState.maxValueTimeGap)
            ?.forEach { valueGroup ->
                if (graphState.showMinMaxValues) {
                    val minMaxColor = when (theme) {
                        Theme.LIGHT -> AirthingsColors.MINT
                        Theme.DARK -> AirthingsColors.STRONGHOLD
                    }
                    data.addDataSet(HighValuesDataSet(sensorValues = valueGroup, minMaxColor = minMaxColor.toArgb()))
                    data.addDataSet(LowValuesDataSet(sensorValues = valueGroup, backgroundColor = surfaceColor))
                }
                data.addDataSet(
                    AverageValuesDataSet(
                        sensorValues = valueGroup,
                        highlightEnabled = true,
                        showColors = graphState.showColors,
                        sensorType = sensorType,
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
