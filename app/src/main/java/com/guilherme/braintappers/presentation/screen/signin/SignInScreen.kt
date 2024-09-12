package com.guilherme.braintappers.presentation.screen.signin

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.SignInWithEmailScreen
import com.guilherme.braintappers.presentation.component.Auth

@Composable
fun SignInScreen(
    navController: NavHostController
) {
    Auth(
        title = stringResource(id = R.string.sign_in_title),
        onContinueWithEmailClick = { navController.navigate(SignInWithEmailScreen) },
        onContinueWithGoogleClick = {},
        labelText = stringResource(R.string.sign_in_label),
        actionText = stringResource(R.string.sign_in_action_text),
        onTextClick = { navController.navigate(com.guilherme.braintappers.navigation.SignUpScreen) }
    )
}