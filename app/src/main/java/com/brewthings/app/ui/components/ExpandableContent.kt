package com.brewthings.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R

@Composable
fun ExpandableContent(
    isExpanded: Boolean = false,
    topContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit = {},
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
            val iconId = if (expandedState.value) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down
            Icon(
                imageVector = ImageVector.vectorResource(id = iconId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        // Using Crossfade to transition between collapsed and expanded content
        Crossfade(
            label = "ExpandableCardContent",
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutLinearInEasing
            ),
            targetState = expandedState.value,
            modifier = Modifier.fillMaxWidth()
        ) { isExpanded ->
            if (isExpanded) {
                expandedContent()
            } else {
                collapsedContent()
            }
        }
    }
}