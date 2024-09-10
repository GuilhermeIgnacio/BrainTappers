package com.guilherme.braintappers.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.domain.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpWithEmailState(
    val emailTextField: String = "",
    val confirmEmailTextField: String = "",
    val passwordTextField: String = "",
    val confirmPasswordTextField: String = ""
)

sealed interface SignUpWithEmailEvents {
    data class OnEmailTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnConfirmEmailTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnPasswordTextFieldChanged(val value: String) : SignUpWithEmailEvents
    data class OnConfirmPasswordTextFieldChanged(val value: String) : SignUpWithEmailEvents
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
                            emailTextField = event.value
                        )
                    }
                }
            }

            is SignUpWithEmailEvents.OnConfirmEmailTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        confirmEmailTextField = event.value
                    ) }
                }
            }


            is SignUpWithEmailEvents.OnPasswordTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        passwordTextField = event.value
                    ) }
                }
            }

            is SignUpWithEmailEvents.OnConfirmPasswordTextFieldChanged -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        confirmPasswordTextField = event.value
                    ) }
                }
            }
        }
    }

}