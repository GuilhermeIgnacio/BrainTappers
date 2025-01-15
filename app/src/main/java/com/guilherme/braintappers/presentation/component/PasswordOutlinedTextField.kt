package com.guilherme.braintappers.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun PasswordOutlinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    isEnabled: Boolean = true,
    errorSupportingText: String
) {
    var passWordVisible by rememberSaveable { mutableStateOf(false) }
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(fontFamily = poppinsFamily),
        enabled = isEnabled,
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = poppinsFamily
            )
        },
        visualTransformation = if (passWordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = primaryColor.copy(alpha = .5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            selectionColors = TextSelectionColors(
                handleColor = primaryColor,
                backgroundColor = LocalTextSelectionColors.current.backgroundColor
            )
        ),
        maxLines = 1,
        supportingText = {
            Crossfade(targetState = isError, label = "") { isError ->
                if (isError) {
                    Text(text = errorSupportingText, fontFamily = poppinsFamily, color = Color.Red)
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