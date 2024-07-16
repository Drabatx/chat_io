package com.drabatx.chatio.presentation.view.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.drabatx.chatio.R

@Composable
fun MessageDialog(
    title: String,
    text: String,
    icon: ImageVector? = null,
    showDialog: Boolean,
    onConfirm: () -> Unit = {},
    primaryButtonText: String? = null,
    secondaryButtonText: String? = null,
    onSecondaryButton: (() -> Unit)? = null
) {
    var showDialogR by remember { mutableStateOf(showDialog) }
    if (showDialogR) {
        AlertDialog(
            icon = {
                icon?.let {
                    Icon(icon, contentDescription = stringResource(R.string.example_icon))
                }
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            onDismissRequest = {
                showDialogR = false
            }, confirmButton = {
                primaryButtonText?.let {
                    TextButton(
                        onClick = {
                            onConfirm()
                            showDialogR = false
                        }
                    ) {
                        Text(it)
                    }
                }

            },
            dismissButton = {
                secondaryButtonText?.let {
                    TextButton(
                        onClick = {
                            onSecondaryButton?.invoke() // Ejecuta la acción del botón secundario
                            showDialogR = false
                        }
                    ) {
                        Text(it)
                    }
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}