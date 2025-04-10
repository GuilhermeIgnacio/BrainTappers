package com.guilherme.braintappers.presentation.screen.signin

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.SignInWithEmailScreen
import com.guilherme.braintappers.presentation.component.Auth
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavHostController
) {

    val viewModel = koinViewModel<SignInViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackBarMessage) {
        state.snackBarMessage?.let {
            val foo = snackBarHostState.showSnackbar(message = it)
            when (foo) {
                SnackbarResult.Dismissed -> {
                    viewModel.clearSnackBar()
                }
                SnackbarResult.ActionPerformed -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { _ ->
        Auth(
            title = stringResource(id = R.string.sign_in_title),
            onContinueWithEmailClick = { navController.navigate(SignInWithEmailScreen) },
            onContinueWithGoogleClick = {
                onEvent(SignInEvents.OnSignInWithGoogleClick(navController = navController))
            },
            labelText = stringResource(R.string.sign_in_label),
            actionText = stringResource(R.string.sign_in_action_text),
            onTextClick = { navController.navigate(com.guilherme.braintappers.navigation.SignUpScreen) }
        )
    }

    CustomCircularProgressIndicator(visible = state.isLoading)

}