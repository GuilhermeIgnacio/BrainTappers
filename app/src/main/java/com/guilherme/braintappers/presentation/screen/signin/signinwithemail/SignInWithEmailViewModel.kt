package com.guilherme.braintappers.presentation.screen.signin.signinwithemail

import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.domain.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class SignInWithEmailState(
    val emailTextField: String = "",
    val passwordTextField: String = ""
)

sealed interface SignInWithEmailEvents {
    data class OnEmailTextFieldChanged(val value: String) : SignInWithEmailEvents
    data class OnPasswordTextFieldChanged(val value: String) : SignInWithEmailEvents
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
        }
    }

}