package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder

class GraphScreenViewModel : ViewModel() {
    private val name: String? = ParameterHolder.Graph.name
    private val macAddress: String = ParameterHolder.Graph.macAddress ?: error("macAddress is required")

    var screenState: GraphScreenState by mutableStateOf(
        GraphScreenState(
            title = name ?: macAddress,
            pillMacAddress = macAddress,
        )
    )
        private set
}
