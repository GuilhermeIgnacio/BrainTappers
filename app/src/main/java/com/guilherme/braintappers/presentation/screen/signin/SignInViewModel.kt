package com.guilherme.braintappers.presentation.screen.signin

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.guilherme.braintappers.domain.FirebaseGoogleAuthError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInState(
    val isLoading: Boolean = false,
    val snackBarMessage: String? = null
)

sealed interface SignInEvents {
    data class OnSignInWithGoogleClick(val navController: NavHostController) : SignInEvents
}

class SignInViewModel(private val firebase: FirebaseRepository) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignInEvents) {
        when (event) {
            is SignInEvents.OnSignInWithGoogleClick -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }

                    when (val result = firebase.signUpWithGoogle()) {

                        is Result.Success -> {

                            event.navController.navigate(HomeScreen)
                            _state.update { it.copy(isLoading = false) }

                        }

                        is Result.Error -> {


                            _state.update { it.copy(isLoading = false) }

                            val errorMessage = when (result.error) {

                                FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_USER -> "Error: Invalid User"

                                FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_CREDENTIALS -> "Error: Invalid Credentials"

                                FirebaseGoogleAuthError.FIREBASE_AUTH_USER_COLLISION -> "Error: there already exists an account with the email address asserted by the credential."

                                FirebaseGoogleAuthError.FIREBASE_NETWORK -> "A network error (such as timeout, interrupted connection or unreachable host) has occurred."

                                FirebaseGoogleAuthError.GET_CREDENTIAL -> "Get Credential Error"

                                FirebaseGoogleAuthError.GET_CREDENTIAL_CANCELLATION -> "Operation cancelled by user."

                                FirebaseGoogleAuthError.NO_CREDENTIAL -> "No Google accounts found on this device. Please add a Google account to proceed."

                                FirebaseGoogleAuthError.UNKNOWN -> "Unknown error, please restart the app or try later."

                            }

                            _state.update { it.copy(snackBarMessage = errorMessage) }

                        }

                    }
                }
            }
        }
    }

    fun clearSnackBar() {
        _state.update { it.copy(snackBarMessage = null) }
    }
}