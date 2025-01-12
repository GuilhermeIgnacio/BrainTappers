package com.guilherme.braintappers.presentation.screen.signin

import androidx.compose.material3.SnackbarHostState
import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
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

                            val snackBar = _state.value.snackbarHostState
                            _state.update { it.copy(isLoading = false) }

                            when (result.error) {

                                FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_USER -> {
                                    snackBar.showSnackbar(
                                        message = "Error: Invalid User"
                                    )
                                }

                                FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_CREDENTIALS -> {
                                    snackBar.showSnackbar(
                                        message = "Error: Invalid Credentials"
                                    )
                                }

                                FirebaseGoogleAuthError.FIREBASE_AUTH_USER_COLLISION -> {
                                    snackBar.showSnackbar(
                                        message = "Error: there already exists an account with the email address asserted by the credential."
                                    )
                                }

                                FirebaseGoogleAuthError.FIREBASE_NETWORK -> {
                                    snackBar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred."
                                    )
                                }

                                FirebaseGoogleAuthError.GET_CREDENTIAL -> {
                                    snackBar.showSnackbar(
                                        message = "Get Credential Error"
                                    )
                                }

                                FirebaseGoogleAuthError.UNKNOWN -> {
                                    snackBar.showSnackbar(
                                        message = "Unknown error, please restart the app or try later."
                                    )
                                }


                            }
                        }

                    }
                }
            }
        }
    }
}