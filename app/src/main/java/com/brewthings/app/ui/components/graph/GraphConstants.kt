package com.brewthings.app.ui.components.graph

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.Congruence
import com.brewthings.app.ui.theme.Grey_Mercury
import com.brewthings.app.ui.theme.Grey_Nevada
import com.brewthings.app.ui.theme.Light_Grey
import com.brewthings.app.ui.theme.Shark

/**
 * Constants used in the chart.
 */
object GraphConstants {
    /**
     * Default sizes for the chart.
     */
    object Size {
        val ICON_ON_GRAPH_SIZE = 32.dp
        val DOUBLE_SPACING = 16.dp
        val LINE_WIDTH = 6.dp
        val MIN_HEIGHT = 200.dp
        val BOTTOM_SPACING: Dp = 6.dp
        val BOTTOM_PADDING: Dp = 12.dp
        val Y_LABEL_HORIZONTAL_PADDING: Dp = 10.dp
        val LIMIT_LABEL_HORIZONTAL_PADDING: Dp = 4.dp
        val LIMIT_LABEL_VERTICAL_PADDING: Dp = 2.dp
        val RIGHT_AXIS_PADDING: Dp = 6.dp
        val LABEL_CORNER_RADIUS: Dp = 10.dp
        val GRID_LINE_PADDING: Dp = 12.dp
        val DASHED_LINE_LENGTH = 4.dp
        val DASHED_LINE_WIDTH = 0.5.dp
        val OVER_VIEW_CARD_BORDER = 1.6.dp

        /**
         * Default sizes configuration for the chart's value marker.
         */
        object Marker {
            val CIRCLE_RADIUS = 20.dp
            val CIRCLE_BORDER_WIDTH = 3.dp
            val CIRCLE_ELEVATION = 8.dp
            val INDICATOR_SIZE = 15.dp
            val INDICATOR_PADDING_TOP = 10.dp
            val INDICATOR_PADDING_BOTTOM = 50.dp
        }
    }

    object Color {
        val bgLabelsLight = Light_Grey
        val bgLabelsDark = Shark
        val axisText = Congruence
        val axisGrid = Grey_Nevada
        val indicatorLight = Shark
        val indicatorDark = Grey_Mercury
    }
}
