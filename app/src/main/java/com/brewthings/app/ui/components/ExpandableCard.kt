package com.brewthings.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R

@Composable
fun ExpandableCard(
    isExpanded: Boolean = false,
    topContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    onAnimationFinished: ((isExpanded: Boolean) -> Unit)? = null,
) {
    val expandedState = remember { mutableStateOf(isExpanded) }
    Column(
        modifier = Modifier.animateContentSize(
            finishedListener = { _, _ -> onAnimationFinished?.invoke(expandedState.value) }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expandedState.value = !expandedState.value })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                topContent()
            }
            val iconId = if (expandedState.value) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up
            Icon(
                imageVector = ImageVector.vectorResource(id = iconId),
                contentDescription = null,
                tint = Color.Blue,
            )
        }
        if (expandedState.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                expandedContent()
            }
        }
    }
}
