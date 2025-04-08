@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.component.BackgroundNavigationBar
import com.brewthings.app.ui.component.BackgroundStatusBar
import com.brewthings.app.ui.component.BrewCompositionAction
import com.brewthings.app.ui.component.TopAppBarBackButton
import com.brewthings.app.ui.component.TopAppBarTitle
import com.brewthings.app.ui.component.graph.Graph
import com.brewthings.app.ui.component.insights.InsightsPager
import com.brewthings.app.ui.converter.toLabel
import com.brewthings.app.ui.navigation.Router
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun GraphScreen(
    router: Router,
    destination: String,
    viewModel: GraphScreenViewModel = koinViewModel(qualifier = named(destination)),
) {
    GraphScreen(
        screenState = viewModel.screenState,
        onBackClick = { router.back() },
        onBrewClicked = { brew ->
            router.goToBrewComposition(brew)
        },
        viewModel::toggleDataType,
        viewModel::onGraphSelect,
        viewModel::onPagerSelect,
        viewModel::setIsOG,
        viewModel::setIsFG,
        viewModel::setFeeding,
        viewModel::deleteMeasurement,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GraphScreen(
    screenState: GraphState,
    onBackClick: () -> Unit,
    onBrewClicked: (Brew) -> Unit,
    toggleDataType: (DataType) -> Unit,
    onGraphSelect: (Int?) -> Unit,
    onPagerSelect: (Int) -> Unit,
    setIsOG: (Instant, Boolean) -> Unit,
    setIsFG: (Instant, Boolean) -> Unit,
    setFeeding: (Instant, Boolean) -> Unit,
    deleteMeasurement: (Instant) -> Unit,
) {
    BackgroundStatusBar()
    BackgroundNavigationBar()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val graphData = screenState.graphData
    val insights = screenState.insights
    val feedings = screenState.feedings
    val selectedDataTypes = screenState.selectedDataTypes
    val selectedDataIndex = screenState.selectedDataIndex

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { TopAppBarTitle(screenState.title) },
                navigationIcon = { TopAppBarBackButton(onBackClick) },
                scrollBehavior = scrollBehavior,
                actions = {
                    val lockBrew = screenState.brew
                    if (lockBrew != null) {
                        BrewCompositionAction {
                            onBrewClicked(lockBrew)
                        }
                    }
                },
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
                        selectedDataTypes = selectedDataTypes,
                        toggleDataType = toggleDataType,
                    )
                }
            }
            item {
                if (graphData != null) {
                    Graph(
                        data = graphData,
                        selectedIndex = selectedDataIndex,
                        onSelect = onGraphSelect,
                    )
                }
            }
            item {
                if (
                    insights != null &&
                    selectedDataIndex != null // Hide if no selected data
                ) {
                    InsightsPager(
                        dataTypes = selectedDataTypes,
                        insights = insights,
                        feedings = feedings,
                        selectedIndex = selectedDataIndex,
                        onSelect = onPagerSelect,
                        setIsOG = setIsOG,
                        setIsFG = setIsFG,
                        setFeeding = setFeeding,
                        showCardActions = screenState.showInsightsCardActions,
                        deleteMeasurement = deleteMeasurement,
                    )
                }
            }
        }
    }
}

@Composable
fun DataTypeSelector(
    options: List<DataType>,
    selectedDataTypes: List<DataType>,
    toggleDataType: (DataType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            onClick = { expanded = !expanded },
        ) {
            Text(text = stringResource(id = R.string.graph_data_label_data_types))

            Icon(
                modifier = Modifier.padding(start = 4.dp),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            // Each DropdownMenuItem represents an option in the dropdown
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        toggleDataType(option)
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Checkbox(
                                checked = option in selectedDataTypes,
                                onCheckedChange = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = option.toLabel())
                        }
                    },
                )
            }
        }
    }
}
