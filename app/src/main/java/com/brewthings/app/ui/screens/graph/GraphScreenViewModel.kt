package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GraphScreenViewModel : ViewModel() {
    var screenState: GraphScreenState by mutableStateOf(GraphScreenState())
        private set
}
