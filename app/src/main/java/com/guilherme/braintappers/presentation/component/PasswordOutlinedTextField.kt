package com.guilherme.braintappers.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.guilherme.braintappers.ui.theme.primaryColor

@Composable
fun PasswordOutlinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    isError: Boolean,
    errorSupportingText: String
) {
    var passWordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        visualTransformation = if (passWordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
        ),
        maxLines = 1,
        isError = isError,
        supportingText = {
            Crossfade(targetState = isError) { isError ->
                if (isError) {
                    Text(text = errorSupportingText)
                }
            }
        },
        trailingIcon = {

            val description = if (passWordVisible) "Hide Password" else "Show Password"

            AnimatedVisibility(visible = value.isNotEmpty()) {

                IconButton(onClick = { passWordVisible = !passWordVisible }) {
                    Crossfade(targetState = passWordVisible, label = "") { isVisible ->

                        val icon =
                            if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(imageVector = icon, contentDescription = description)

                    }
                }

            }

        }
    )
}