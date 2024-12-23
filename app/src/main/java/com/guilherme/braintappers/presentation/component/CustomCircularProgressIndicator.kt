package com.guilherme.braintappers.presentation.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.guilherme.braintappers.ui.theme.primaryColor

@Composable
fun CustomCircularProgressIndicator(visible: Boolean) {
    if (visible) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                trackColor = primaryColor,
            )
        }
    }
}