@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brewthings.app.R
import com.brewthings.app.data.model.DataType
import com.brewthings.app.ui.theme.Size
import org.koin.androidx.compose.koinViewModel

@Composable
fun GraphScreen(
    navController: NavController,
    viewModel: GraphScreenViewModel = koinViewModel(),
) {
    GraphScreen(
        screenState = viewModel.screenState,
        onBackClick = { navController.popBackStack() },
        viewModel::toggleSeries,
        viewModel::onSelect,
    )
}

@Composable
fun GraphScreen(
    screenState: GraphScreenState,
    onBackClick: () -> Unit,
    toggleSeries: (DataType) -> Unit,
    onSelect: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GraphTopBar(
                scrollBehavior = scrollBehavior,
                title = screenState.title,
                onBackClick = onBackClick
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                Graph(
                    modifier = Modifier.height(Size.Graph.HEIGHT),
                    enabledTypes = screenState.enabledTypes,
                    graphData = screenState.graphData,
                    selectedIndex = screenState.selectedInsights,
                    toggleSeries = toggleSeries,
                    onSelect = onSelect
                )
            }
            item {
                GraphInsightsPager(screenState = screenState, onSelect = onSelect)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GraphInsightsPager(
    screenState: GraphScreenState,
    onSelect: (Int) -> Unit,
) {
    val startIndex = screenState.selectedInsights.takeIf { it != -1 } ?: return

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { screenState.insights.count() },
    )

    LaunchedEffect(pagerState) {
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
            GraphInsights(data = screenState.insights[index])
        }
    }

    LaunchedEffect(screenState.selectedInsights) {
        pagerState.animateScrollToPage(screenState.selectedInsights)
    }
}

@Composable
fun GraphTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
