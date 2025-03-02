@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.Device

@Composable
fun EditNameBottomSheet(
    device: Device,
    onDismiss: () -> Unit,
    onDeviceNameUpdate: (String) -> Unit,
) {
    var name by remember { mutableStateOf(device.name ?: "") }
    val sheetState = SheetState(skipPartiallyExpanded = true, density = Density(1f))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = .5f),
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .imePadding()
                .padding(vertical = 32.dp, horizontal = 24.dp), // Inner padding,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(id = R.string.edit_name_tooltip)) },
                readOnly = false,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
            )

            PrimaryButton(
                isEnabled = isValidName(oldName = device.name, newName = name),
                text = stringResource(id = R.string.button_save),
                onClick = {
                    onDeviceNameUpdate(name)
                    onDismiss()
                },
            )
        }
    }
}

private fun isValidName(oldName: String?, newName: String?): Boolean =
    newName?.trim()?.let { it.isNotEmpty() && it != oldName } ?: false
