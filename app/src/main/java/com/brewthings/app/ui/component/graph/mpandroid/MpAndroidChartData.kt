package com.brewthings.app.ui.component.graph.mpandroid

import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * A [LineData] that sorts the data sets by visibility.
 *
 * This workaround is needed because the MPAndroidChart library has a bug: when you add the InvisibleDataSet last, it
 * may somehow affect the drawing of the VisibleDataSet lines, potentially due to internal optimizations or the
 * rendering pipeline.
 * Since the issue doesn't occur if the InvisibleDataSet is added first. This class ensures that the invisible dataset
 * is added before any visible datasets.
 */
class MpAndroidChartData(
    dataSets: List<ILineDataSet>,
) : LineData(
    dataSets.sortedBy { it !is InvisibleDataSet },
)
