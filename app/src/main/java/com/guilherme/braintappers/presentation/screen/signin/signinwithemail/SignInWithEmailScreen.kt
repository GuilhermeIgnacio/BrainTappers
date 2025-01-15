package com.guilherme.braintappers.presentation.screen.signin.signinwithemail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import com.guilherme.braintappers.presentation.component.CustomTopAppBar
import com.guilherme.braintappers.presentation.component.EmailOutlinedTextField
import com.guilherme.braintappers.presentation.component.PasswordOutlinedTextField
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInWithEmailScreen(navController: NavHostController) {

    val viewModel = koinViewModel<SignInWithEmailViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {

            CustomTopAppBar(
                title = "Sign In",
                onReturnClick = { navController.navigateUp() }
            )

            EmailOutlinedTextField(
                modifier = Modifier,
                value = state.emailTextField,
                onValueChange = {
                    onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(it))
                },
                placeholder = stringResource(id = R.string.authenticate_with_email_placeholder),
                isError = false,
                errorSupportingText = ""
            )

            PasswordOutlinedTextField(
                modifier = Modifier,
                value = state.passwordTextField,
                onValueChange = { onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(it)) },
                placeholder = stringResource(id = R.string.authenticate_with_email_password_placeholder),
                isError = false,
                errorSupportingText = ""
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(SignInWithEmailEvents.OnNextButtonClick(navController))
                },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.Black
                ),
                enabled = state.emailTextField.isNotEmpty() && state.passwordTextField.isNotEmpty()
            ) {
                Text(
                    text = "Next",
                    fontFamily = poppinsFamily
                )
            }

            TextButton(
                onClick = { onEvent(SignInWithEmailEvents.OnForgotPasswordButtonClicked) }
            ) {
                Text(
                    text = "Forgot Password?",
                    fontFamily = poppinsFamily
                )
            }

        }
    }

    CustomCircularProgressIndicator(state.isLoading)

    ResetPasswordModalBottomSheet(state, onEvent)

}