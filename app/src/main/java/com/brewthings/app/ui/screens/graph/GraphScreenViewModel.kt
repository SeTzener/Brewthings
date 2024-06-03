package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.brewthings.app.data.model.RaptPillInfo

class GraphScreenViewModel(
    pillInfo: RaptPillInfo
) : ViewModel() {
    var screenState: GraphScreenState by mutableStateOf(
        GraphScreenState(
            title = pillInfo.name,
            pillMacAddress = pillInfo.macAddress
        )
    )
        private set
}
