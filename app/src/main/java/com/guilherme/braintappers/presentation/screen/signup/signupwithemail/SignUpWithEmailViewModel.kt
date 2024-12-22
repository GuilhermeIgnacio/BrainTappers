package com.guilherme.braintappers.presentation.screen.signup.signupwithemail

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.FirebaseError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class SignUpWithEmailState(
    val emailTextField: String = "",
    val confirmEmailTextField: String = "",
    val passwordTextField: String = "",
    val confirmPasswordTextField: String = "",
    val isLoading: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)

sealed interface SignUpWithEmailEvents {
    data class OnEmailTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnConfirmEmailTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnPasswordTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnConfirmPasswordTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnNextButtonClick(val value: NavHostController) : SignUpWithEmailEvents
}

class SignUpWithEmailViewModel(private val firebase: FirebaseRepository) : ViewModel() {
    private val _state = MutableStateFlow(SignUpWithEmailState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignUpWithEmailEvents) {
        when (event) {
            is SignUpWithEmailEvents.OnEmailTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            emailTextField = event.value.lowercase(Locale.ROOT)
                        )
                    }
                }
            }

            is SignUpWithEmailEvents.OnConfirmEmailTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            confirmEmailTextField = event.value.lowercase(Locale.ROOT)
                        )
                    }
                }
            }


            is SignUpWithEmailEvents.OnPasswordTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            passwordTextField = event.value
                        )
                    }
                }
            }

            is SignUpWithEmailEvents.OnConfirmPasswordTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            confirmPasswordTextField = event.value
                        )
                    }
                }
            }

            is SignUpWithEmailEvents.OnNextButtonClick -> {
                //Todo: Loading State
                viewModelScope.launch {
                    val email = _state.value.emailTextField
                    val password = _state.value.passwordTextField

                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }

                    when (val result = firebase.signUpWithEmail(email, password)) {

                        is Result.Success -> {
                            event.value.navigate(HomeScreen)
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }

                        is Result.Error -> {

                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }

                            val snackBar = _state.value.snackbarHostState

                            when (result.error) {

                                FirebaseError.UNKNOWN -> {
                                    snackBar.showSnackbar(
                                        message = "Unknown error, please restart the app or try later.",
                                    )
                                }

                                FirebaseError.FIREBASE_AUTH_USER_COLLISION -> {
                                    snackBar.showSnackbar(
                                        message = "The email address is already in use by another account.",
                                    )
                                }

                                FirebaseError.FIREBASE_NETWORK -> {
                                    snackBar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
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