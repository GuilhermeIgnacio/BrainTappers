package com.guilherme.braintappers.presentation.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guilherme.braintappers.ui.theme.primaryColor

@Composable
fun EmailOutlinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)?,
    isError: Boolean,
    errorSupportingText: String
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
        ),
        supportingText = {
            //Todo: Add Crossfade Animation Label
            Crossfade(targetState = isError, label = "") { isError ->
                if (isError) {
                    Text(text = errorSupportingText)
                }
            }
        },
        isError = isError
    )
}