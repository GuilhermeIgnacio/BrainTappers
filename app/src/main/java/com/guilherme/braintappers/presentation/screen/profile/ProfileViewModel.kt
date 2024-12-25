package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.WelcomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait

data class ProfileState(
    val user: FirebaseUser? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)

sealed interface ProfileEvents {
    data class OnConfirmSignOut(val value: NavController) : ProfileEvents
    data class OnConfirmAccountDeletion(val value: NavController) : ProfileEvents
}

class ProfileViewModel(private val firebase: FirebaseRepository) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    user = firebase.currentUser()
                )
            }
        }
    }

    fun onEvent(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.OnConfirmSignOut -> {
                viewModelScope.launch {
                    firebase.signOut()
                    event.value.navigate(WelcomeScreen)
                }
            }

            is ProfileEvents.OnConfirmAccountDeletion -> {
                viewModelScope.launch {
                    when (val result = firebase.deleteAccount()) {

                        is Result.Success -> {
                            event.value.navigate(WelcomeScreen)
                        }

                        is Result.Error -> {
                            val snackBar = _state.value.snackbarHostState
                            when (result.error) {

                                FirebaseAccountDeletion.FIREBASE_AUTH_INVALID_USER -> {
                                    snackBar.showSnackbar(
                                        message = "Invalid User Error: The current user's account has been disabled, deleted, or its credentials are no longer valid."
                                    )
                                }

                                FirebaseAccountDeletion.FIREBASE_AUTH_RECENT_LOGIN_REQUIRED -> {
                                    snackBar.showSnackbar(
                                        message = "Error: The user's last sign-in time does not meet the security threshold."
                                    )
                                }

                                FirebaseAccountDeletion.FIREBASE_NETWORK -> {
                                    snackBar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                    )
                                }

                                FirebaseAccountDeletion.UNKNOWN -> {
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