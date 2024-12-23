package com.guilherme.braintappers.presentation.screen.signin.signinwithemail

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.FirebaseSignInWithEmailAndPasswordError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SignInWithEmailState(
    val emailTextField: String = "",
    val passwordTextField: String = "",
    val isLoading: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)

sealed interface SignInWithEmailEvents {
    data class OnEmailTextFieldChanged(val value: String) : SignInWithEmailEvents
    data class OnPasswordTextFieldChanged(val value: String) : SignInWithEmailEvents
    data class OnNextButtonClick(val value: NavHostController) : SignInWithEmailEvents
}

class SignInWithEmailViewModel(private val firebase: FirebaseRepository) : ViewModel() {

    private val _state = MutableStateFlow(SignInWithEmailState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignInWithEmailEvents) {
        when (event) {
            is SignInWithEmailEvents.OnEmailTextFieldChanged -> {
                _state.update {
                    it.copy(
                        emailTextField = event.value
                    )
                }
            }

            is SignInWithEmailEvents.OnPasswordTextFieldChanged -> {
                _state.update {
                    it.copy(
                        passwordTextField = event.value
                    )
                }
            }

            is SignInWithEmailEvents.OnNextButtonClick -> {
                viewModelScope.launch {

                    _state.update { it.copy(isLoading = true) }

                    val email = _state.value.emailTextField
                    val password = _state.value.passwordTextField

                    when (val result = firebase.signInWithEmail(email, password)) {

                        is Result.Success -> {
                            event.value.navigate(HomeScreen)
                            _state.update { it.copy(isLoading = false) }
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false) }
                            val snackBar = _state.value.snackbarHostState
                            when (result.error) {

                                FirebaseSignInWithEmailAndPasswordError.FIREBASE_AUTH_INVALID_CREDENTIALS -> {
                                    snackBar.showSnackbar(
                                        message = "Error: Invalid user. Please check your credentials and try again."
                                    )
                                }

                                FirebaseSignInWithEmailAndPasswordError.FIREBASE_NETWORK -> {
                                    snackBar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                    )
                                }

                                FirebaseSignInWithEmailAndPasswordError.UNKNOWN -> {
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