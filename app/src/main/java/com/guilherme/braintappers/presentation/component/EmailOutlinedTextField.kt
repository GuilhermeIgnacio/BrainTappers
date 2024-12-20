package com.guilherme.braintappers.presentation.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.guilherme.braintappers.R
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun EmailOutlinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    errorSupportingText: String
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = poppinsFamily
            )
        },
        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(fontFamily = poppinsFamily),
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
        supportingText = {
            Crossfade(targetState = isError, label = "") { isError ->
                if (isError) {
                    Text(text = errorSupportingText, fontFamily = poppinsFamily)
                }
            }
        },
    )

}