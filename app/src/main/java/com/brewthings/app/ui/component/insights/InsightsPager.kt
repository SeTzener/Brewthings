package com.brewthings.app.ui.component.insights

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.util.Logger
import kotlinx.datetime.Instant

@Composable
fun InsightsPager(
    dataTypes: List<DataType>,
    insights: List<RaptPillInsights>,
    selectedIndex: Int,
    showCardActions: Boolean,
    feedings: List<Instant>,
    onSelect: (Int) -> Unit,
    setIsOG: (Instant, Boolean) -> Unit,
    setIsFG: (Instant, Boolean) -> Unit,
    setFeeding: (Instant, Boolean) -> Unit,
    deleteMeasurement: (Instant) -> Unit,
) {
    val logger by remember { mutableStateOf(Logger("InsightsPager")) }

    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { insights.count() },
    )

    LaunchedEffect(pagerState) {
        logger.info("Pager: index=$selectedIndex animated=false")
        snapshotFlow { pagerState.targetPage }.collect { page ->
            onSelect(page)
        }
    }
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp), // Adjust padding for peeking
        state = pagerState,
    ) { index ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            InsightsCard(
                dataTypes = dataTypes,
                data = insights[index],
                showActions = showCardActions,
                setIsOG = setIsOG,
                setIsFG = setIsFG,
                setFeeding = setFeeding,
                feedings = feedings,
                deleteMeasurement = deleteMeasurement,
            )
        }
    }

    LaunchedEffect(selectedIndex) {
        logger.info("Pager: index=$selectedIndex animated=true")
        pagerState.animateScrollToPage(
            page = selectedIndex,
            animationSpec = tween(500, easing = LinearEasing),
        )
    }
}
