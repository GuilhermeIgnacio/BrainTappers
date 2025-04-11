package com.guilherme.braintappers.presentation.screen.signup

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.SignInScreen
import com.guilherme.braintappers.navigation.SignUpWithEmailScreen
import com.guilherme.braintappers.presentation.component.Auth
import com.guilherme.braintappers.ui.theme.primaryColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController
) {

    val viewModel = koinViewModel<SignUpViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackBarMessage) {
        state.snackBarMessage?.let {
            val result = snackBarHostState.showSnackbar(message = it)
            when (result) {
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
            title = stringResource(id = R.string.sign_up_title),
            onContinueWithEmailClick = { navController.navigate(SignUpWithEmailScreen) },
            onContinueWithGoogleClick = {
                onEvent(SignUpEvents.OnSignUpWithGoogleClick(navController))
            },
            labelText = stringResource(id = R.string.sign_up_label),
            actionText = stringResource(id = R.string.sign_up_action_text),
            onTextClick = { navController.navigate(SignInScreen) }
        )
    }

    if (state.isLoading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                trackColor = primaryColor,
            )
        }
    }

}