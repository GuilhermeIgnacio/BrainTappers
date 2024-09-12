package com.guilherme.braintappers.presentation.screen.signup

import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.guilherme.braintappers.domain.FirebaseRepository
import kotlinx.coroutines.launch

sealed interface SignUpEvents {
    data class OnSignUpWithGoogleClick(val value: Credential) : SignUpEvents
}

class SignUpViewModel(
    private val firebase: FirebaseRepository
) : ViewModel() {

    fun onEvent(event: SignUpEvents) {
        when (event) {
            is SignUpEvents.OnSignUpWithGoogleClick -> {
                viewModelScope.launch {
                    val credential = event.value

                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                    firebase.signUpWithGoogle(googleIdToken.idToken)
                }
            }
        }
    }

}