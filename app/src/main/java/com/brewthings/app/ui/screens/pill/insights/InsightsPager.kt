package com.brewthings.app.ui.screens.pill.insights

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.screens.pill.GraphScreenLogger
import com.brewthings.app.ui.screens.pill.data.DataType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InsightsPager(
    state: InsightsState,
    dataType: DataType,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { state.insights.count() },
    )

    LaunchedEffect(pagerState) {
        GraphScreenLogger.logPager(selectedIndex, animated = false)
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
                .padding(8.dp)
        ) {
            InsightsCard(state.insights[index])
        }
    }

    LaunchedEffect(selectedIndex) {
        GraphScreenLogger.logPager(selectedIndex, animated = true)
        pagerState.animateScrollToPage(
            page = selectedIndex,
            animationSpec = tween(500, easing = LinearEasing)
        )
    }
}
