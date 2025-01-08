package com.guilherme.braintappers.presentation.screen.signin.signinwithemail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

@OptIn(ExperimentalMaterial3Api::class)
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

    if (state.recoverModalBottomSheetVisibility) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(SignInWithEmailEvents.DismissRecoverModalBottomSheet) },
        ) {


            AnimatedContent(
                targetState = state.resetPasswordState
            ) { resetPasswordState ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    when (resetPasswordState) {
                        ResetPasswordState.NONE -> {

                            EmailOutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.recoverEmailTextField,
                                onValueChange = {
                                    onEvent(
                                        SignInWithEmailEvents.OnRecoverTextFieldChanged(
                                            it
                                        )
                                    )
                                },
                                placeholder = "Email",
                                isError = false,
                                isEnabled = true,
                                errorSupportingText = ""
                            )

                            Button(
                                modifier = Modifier.align(Alignment.End),
                                onClick = { onEvent(SignInWithEmailEvents.OnSendResetPasswordEmailClicked) },
                                shape = RoundedCornerShape(20),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                    contentColor = Color.Black
                                ),
                                enabled = state.recoverEmailTextField.isNotEmpty()
                            ) {
                                Text(
                                    text = "Send Password Reset Email",
                                    fontFamily = poppinsFamily
                                )
                            }

                        }

                        ResetPasswordState.SUCCESS -> {

                            Icon(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(36.dp),
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = "Success Icon",
                                tint = primaryColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = "Password Reset Email Sent Successfully!",
                                fontFamily = poppinsFamily
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                        }

                        ResetPasswordState.ERROR -> {
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(36.dp),
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error Icon",
                                tint = Color.Red
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = state.resetPasswordErrorMessage,
                                fontFamily = poppinsFamily,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

        }
    }

}