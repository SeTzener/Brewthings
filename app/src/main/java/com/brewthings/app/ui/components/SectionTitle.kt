package com.brewthings.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.Typography

@Composable
fun SectionTitle(title: String) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = title.uppercase(),
        color = MaterialTheme.colorScheme.onBackground,
        style = Typography.bodyMedium,
    )
}
