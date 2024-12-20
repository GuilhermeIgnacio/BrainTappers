package com.guilherme.braintappers.presentation.screen.signup.signupwithemail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.component.CustomTopAppBar
import com.guilherme.braintappers.presentation.component.EmailOutlinedTextField
import com.guilherme.braintappers.presentation.component.PasswordOutlinedTextField
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.isValidEmail
import com.guilherme.braintappers.util.isValidPassword
import com.guilherme.braintappers.util.poppinsFamily
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
        CustomTopAppBar(
            title = stringResource(id = R.string.sign_up_with_email_screen_title),
            onReturnClick = { navController.navigateUp() }
        )

        //Email

        EmailOutlinedTextField(
            modifier = modifier,
            value = state.emailTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(it)) },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.authenticate_with_email_placeholder),
                    fontFamily = poppinsFamily
                )
            },
            isError = state.emailTextField.isNotEmpty() && !state.emailTextField.isValidEmail(),
            errorSupportingText = stringResource(id = R.string.sign_up_with_email_error_supporting_text)
        )


        EmailOutlinedTextField(
            modifier = modifier,
            value = state.confirmEmailTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnConfirmEmailTextFieldChanged(it)) },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_confirm_email_placeholder), fontFamily = poppinsFamily) },
            isError = state.confirmEmailTextField.isNotEmpty() && state.emailTextField != state.confirmEmailTextField,
            errorSupportingText = stringResource(id = R.string.sign_up_with_email_confirm_error_supporting_text)
        )

        //Email

        //Password

        PasswordOutlinedTextField(
            modifier = modifier,
            value = state.passwordTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(it)) },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_password_placeholder), fontFamily = poppinsFamily) },
            isError = state.passwordTextField.isNotEmpty() && !state.passwordTextField.isValidPassword(),
            errorSupportingText = stringResource(id = R.string.sign_up_with_password_error_suporting_text)
        )

        PasswordOutlinedTextField(
            modifier = modifier,
            value = state.confirmPasswordTextField,
            onValueChange = { onEvent(SignUpWithEmailEvents.OnConfirmPasswordTextFieldChanged(it)) },
            placeholder = { Text(text = stringResource(id = R.string.authenticate_with_email_confirm_password_placeholder), fontFamily = poppinsFamily) },
            isError = state.confirmPasswordTextField.isNotEmpty() && state.confirmPasswordTextField != state.passwordTextField,
            errorSupportingText = stringResource(id = R.string.sign_up_with_password_confirm_error_suporting_text)
        )

        //Password

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
                onEvent(SignUpWithEmailEvents.OnNextButtonClick)
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            ),
            enabled = state.emailTextField.isValidEmail() &&
                    state.emailTextField == state.confirmEmailTextField &&
                    state.passwordTextField.isValidPassword() &&
                    state.passwordTextField == state.confirmPasswordTextField
        ) {
            Text(
                text = "Next",
                fontFamily = poppinsFamily
            )
        }

    }
}