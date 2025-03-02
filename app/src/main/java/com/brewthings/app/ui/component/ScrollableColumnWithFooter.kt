@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun ScrollableColumnWithFooter(
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection? = null,
    scrollableContent: @Composable ColumnScope.() -> Unit,
    footer: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .let { modifier ->
                    nestedScrollConnection?.let { nestedScroll ->
                        modifier.nestedScroll(nestedScroll)
                    } ?: modifier
                }
                .verticalScroll(scrollState),
        ) {
            scrollableContent()
            Spacer(modifier = Modifier.weight(1f)) // Pushes footer down if there's space
            footer()
        }
    }
}

@Composable
@Preview
fun ColumnWithStickyFooterPreview() {
    Preview(contentHeight = 150.dp)
}

@Composable
@Preview
fun ScrollableColumnWithFooterPreview() {
    Preview(contentHeight = 800.dp)
}

@Composable
private fun Preview(
    contentHeight: Dp,
    footerHeight: Dp = 100.dp,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BrewthingsTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text("Preview")
                    },
                )
            },
        ) { paddingValues ->
            ScrollableColumnWithFooter(
                modifier = Modifier.padding(paddingValues),
                scrollableContent = {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Yellow)
                            .fillMaxWidth()
                            .height(contentHeight),
                    )
                },
                footer = {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Red)
                            .fillMaxWidth()
                            .height(footerHeight),
                    )
                },
            )
        }
    }
}
