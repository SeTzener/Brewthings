@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
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
        viewModel::onValueSelected,
    )
}

@Composable
fun GraphScreen(
    screenState: GraphScreenState,
    onBackClick: () -> Unit,
    toggleSeries: (DataType) -> Unit,
    onValueSelected: (Any?) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Graph(
                modifier = Modifier.height(Size.Graph.HEIGHT),
                enabledTypes = screenState.enabledTypes,
                graphData = screenState.graphData,
                toggleSeries = toggleSeries,
                onValueSelected = onValueSelected
            )
            screenState.selectedInsights?.also {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GraphInsights(data = it)
                }
            }
        }
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
