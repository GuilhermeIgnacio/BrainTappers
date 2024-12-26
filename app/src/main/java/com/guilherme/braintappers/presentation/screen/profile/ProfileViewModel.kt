package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.data.FirebaseProviderId
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseCurrentUser
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
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val modalBottomSheetVisibility: Boolean = false,
    val emailTextField: String = "",
    val passwordTextField: String = ""
)

sealed interface ProfileEvents {
    data class OnConfirmSignOut(val value: NavController) : ProfileEvents
    data class OnConfirmAccountDeletion(val value: NavController) : ProfileEvents
    data class OnEmailTextFieldValueChanged(val value: String) : ProfileEvents
    data class OnPasswordChanged(val value: String): ProfileEvents
    data object DismissModalBottomSheet: ProfileEvents

    data object ReauthenticateWithEmailAndPassword: ProfileEvents
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

            is ProfileEvents.OnEmailTextFieldValueChanged -> {
                _state.update { it.copy(emailTextField = event.value) }
            }

            is ProfileEvents.OnPasswordChanged -> {
                _state.update { it.copy(passwordTextField = event.value) }
            }

            ProfileEvents.DismissModalBottomSheet -> {
                _state.update { it.copy(modalBottomSheetVisibility = false) }
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

                                    when (val providerIdResult =
                                        firebase.getCurrentUserProviderId()) {
                                        is Result.Success -> {

                                            when (providerIdResult.data) {

                                                FirebaseProviderId.PASSWORD -> {
                                                    _state.update {
                                                        it.copy(
                                                            modalBottomSheetVisibility = true
                                                        )
                                                    }
                                                }

                                                FirebaseProviderId.GOOGLE -> TODO()

                                            }

                                        }

                                        is Result.Error -> {

                                            when (providerIdResult.error) {
                                                FirebaseCurrentUser.NULL_VALUE -> {
                                                    TODO()
                                                }

                                                FirebaseCurrentUser.UNEXPECTED_PROVIDER -> {
                                                    TODO()
                                                }

                                                FirebaseCurrentUser.UNKNOWN -> {
                                                    TODO()
                                                }
                                            }
                                        }
                                    }

//                                    snackBar.showSnackbar(
//                                        message = "Error: The user's last sign-in time does not meet the security threshold."
//                                    )
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

            ProfileEvents.ReauthenticateWithEmailAndPassword -> {
                viewModelScope.launch {

                }
            }
        }
    }

}