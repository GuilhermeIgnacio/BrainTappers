package com.guilherme.braintappers.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.isValidEmail
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpWithEmailScreen(navController: NavHostController) {

    val viewModel = koinViewModel<SignUpWithEmailViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    val modifier = Modifier.padding(start = 8.dp, end = 8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { navController.navigateUp() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.sign_up_with_email_screen_title),
                fontWeight = FontWeight.Bold
            )


        }

        //Email
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = state.emailTextField,
            onValueChange = {
                onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(it))
            },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_placeholder)) },
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = state.confirmEmailTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnConfirmEmailTextFieldChanged(it)) },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_confirm_email_placeholder)) },
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )
        //Email

        //Password
        var passWordVisible by rememberSaveable { mutableStateOf(false) }
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = state.passwordTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(it)) },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_password_placeholder)) },
            visualTransformation = if (passWordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            ),
            maxLines = 1,
            trailingIcon = {

                val description = if (passWordVisible) "Hide Password" else "Show Password"

                AnimatedVisibility(visible = state.passwordTextField.isNotEmpty()) {

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

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = state.confirmPasswordTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnConfirmPasswordTextFieldChanged(it)) },
            placeholder = { Text(text = "Confirm Password") },
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )
        //Password

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {},
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Next"
            )
        }

    }
}