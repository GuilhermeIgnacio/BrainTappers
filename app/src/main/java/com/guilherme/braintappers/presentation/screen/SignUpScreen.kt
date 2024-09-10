package com.guilherme.braintappers.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.SignInScreen
import com.guilherme.braintappers.navigation.SignUpWithEmailScreen
import com.guilherme.braintappers.presentation.component.Auth
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController
) {

    val viewModel = koinViewModel<SignUpViewModel>()
    val onEvent = viewModel::onEvent

    Auth(
        title = stringResource(id = R.string.sign_up_title),
        onContinueWithEmailClick = { navController.navigate(SignUpWithEmailScreen) },
        onContinueWithGoogleClick = { credential ->
            onEvent(SignUpEvents.OnSignUpWithGoogleClick(credential))
        },
        labelText = stringResource(id = R.string.sign_up_label),
        actionText = stringResource(id = R.string.sign_up_action_text),
        onTextClick = { navController.navigate(SignInScreen) }
    )
}