@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screens.pill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brewthings.app.R
import com.brewthings.app.ui.screens.pill.data.DataType
import com.brewthings.app.ui.screens.pill.graph.Graph
import com.brewthings.app.ui.screens.pill.insights.InsightsPager
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun GraphScreen(
    navController: NavController,
    viewModel: GraphScreenViewModel = koinViewModel(),
) {
    GraphScreen(
        screenState = viewModel.screenState,
        onBackClick = { navController.popBackStack() },
        viewModel::selectSeries,
        viewModel::onGraphSelect,
        viewModel::onPagerSelect,
        viewModel::setIsOG,
        viewModel::setIsFG,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GraphScreen(
    screenState: GraphScreenState,
    onBackClick: () -> Unit,
    selectSeries: (DataType) -> Unit,
    onGraphSelect: (Int?) -> Unit,
    onPagerSelect: (Int) -> Unit,
    setIsOG: (Instant, Boolean) -> Unit,
    setIsFG: (Instant, Boolean) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val graphState = screenState.graphState
    val insightsState = screenState.insightsState
    val selectedType = screenState.selectedDataType
    val selectedIndex = screenState.selectedDataIndex

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
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    DataTypeSelector(
                        options = screenState.dataTypes,
                        selected = selectedType,
                        onSelect = selectSeries,
                    )
                }
            }
            item {
                if (graphState != null) {
                    Graph(
                        state = graphState,
                        dataType = selectedType,
                        selectedIndex = selectedIndex,
                        onSelect = onGraphSelect
                    )

                }
            }
            item {
                if (
                    insightsState != null &&
                    selectedIndex != null // Hide if no selected data
                ) {
                    InsightsPager(
                        state = insightsState,
                        selectedIndex = selectedIndex,
                        onSelect = onPagerSelect,
                        setIsOG = setIsOG,
                        setIsFG = setIsFG,
                    )
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

@Composable
fun DataTypeSelector(
    options: List<DataType>,
    selected: DataType,
    onSelect: (DataType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            onClick = { expanded = !expanded },
        ) {
            Text(text = selected.toLabel())

            Icon(
                modifier = Modifier.padding(start = 4.dp),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Each DropdownMenuItem represents an option in the dropdown
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    text = {
                        Text(text = option.toLabel())
                    }
                )
            }
        }
    }
}

@Composable
private fun DataType.toLabel(): String = when (this) {
    DataType.TEMPERATURE -> stringResource(id = R.string.graph_data_label_temp_full)
    DataType.GRAVITY -> stringResource(id = R.string.graph_data_label_gravity)
    DataType.BATTERY -> stringResource(id = R.string.graph_data_label_battery)
}
