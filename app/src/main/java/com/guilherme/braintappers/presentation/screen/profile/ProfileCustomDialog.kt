package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun ProfileCustomDialog(
    visibility: Boolean,
    onDismissRequest: () -> Unit,
    icon: ImageVector,
    iconContentDescription: String,
    title: String,
    text: String,
    onConfirmClick: () -> Unit,
    confirmButtonText: String
) {
    if (visibility) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = Color.Red
                )
            },
            title = {
                Text(text = title, fontFamily = poppinsFamily, color = Color.Red)
            },
            text = {
                Text(text = text, fontFamily = poppinsFamily, textAlign = TextAlign.Center)
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick
                ) {
                    Text(text = confirmButtonText, fontFamily = poppinsFamily, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Dismiss", fontFamily = poppinsFamily)
                }
            }
        )
    }
}