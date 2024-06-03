@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.brewthings.app.R
import com.brewthings.app.data.model.RaptPillInfo
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GraphScreen(
    navController: NavController,
    pill: RaptPillInfo,
    viewModel: GraphScreenViewModel = koinViewModel { parametersOf(pill) },
) {
    GraphScreen(
        screenState = viewModel.screenState,
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun GraphScreen(
    screenState: GraphScreenState,
    onBackClick: () -> Unit,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

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
        title = { Text(title) },
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

@Preview(showBackground = true)
@Composable
fun GraphScreenPreview() {
    GraphScreen(
        screenState = GraphScreenState(title = "My Pill", pillMacAddress = "12:34:56:AB"),
        onBackClick = {}
    )
}
