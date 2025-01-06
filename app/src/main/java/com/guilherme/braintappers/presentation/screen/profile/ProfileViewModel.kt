package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.data.FirebaseProviderId
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseCurrentUser
import com.guilherme.braintappers.domain.FirebaseFirestoreDeleteError
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseReauthenticate
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.WelcomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: FirebaseUser? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val modalBottomSheetVisibility: Boolean = false,
    val emailTextField: String = "",
    val passwordTextField: String = "",
    val isReauthenticateWithEmailAndPasswordError: Boolean = false,
    val errorSupportingText: String = "",
    val isLoading: Boolean = false
)

sealed interface ProfileEvents {
    data class OnConfirmSignOut(val value: NavController) : ProfileEvents
    data class OnConfirmAccountDeletion(val value: NavController) : ProfileEvents
    data object OnConfirmClearHistory : ProfileEvents
    data class OnEmailTextFieldValueChanged(val value: String) : ProfileEvents
    data class OnPasswordChanged(val value: String) : ProfileEvents
    data object DismissModalBottomSheet : ProfileEvents

    data class ReauthenticateWithEmailAndPassword(val value: NavController) : ProfileEvents
}

class ProfileViewModel(
    private val firebase: FirebaseRepository,
    private val firestore: FirebaseFirestoreRepository
) : ViewModel() {

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

            ProfileEvents.OnConfirmClearHistory -> {
                viewModelScope.launch {
                    val snackbar = _state.value.snackbarHostState
                    when (val result = firestore.deleteData()) {
                        is Result.Success -> {
                            println("Delete Data Operation Success")
                            snackbar.showSnackbar(
                                message = "Your history has been completely cleared. All previously saved information has been removed"
                            )
                        }

                        is Result.Error -> {
                            when (result.error) {
                                FirebaseFirestoreDeleteError.FIREBASE_NETWORK -> {
                                    snackbar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                    )
                                }

                                FirebaseFirestoreDeleteError.UNKNOWN -> {
                                    snackbar.showSnackbar(
                                        message = "Unknown error, please restart the app or try later."
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is ProfileEvents.OnEmailTextFieldValueChanged -> {
                _state.update { it.copy(emailTextField = event.value) }
            }

            is ProfileEvents.OnPasswordChanged -> {
                _state.update { it.copy(passwordTextField = event.value) }
            }

            ProfileEvents.DismissModalBottomSheet -> {
                _state.update {
                    it.copy(
                        modalBottomSheetVisibility = false,
                        emailTextField = "",
                        passwordTextField = "",
                        errorSupportingText = "",
                        isReauthenticateWithEmailAndPasswordError = false
                    )
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
                                    reauthenticateUser(event.value, snackBar)
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

            is ProfileEvents.ReauthenticateWithEmailAndPassword -> {
                viewModelScope.launch {

                    _state.update { it.copy(isLoading = true) }

                    val email = _state.value.emailTextField
                    val password = _state.value.passwordTextField

                    when (val result =
                        firebase.reauthenticateWithEmailAndPassword(email, password)
                    ) {

                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    modalBottomSheetVisibility = false,
                                    isLoading = false,
                                    isReauthenticateWithEmailAndPasswordError = false
                                )
                            }

                            event.value.navigate(WelcomeScreen)
                        }

                        is Result.Error -> {

                            _state.update {
                                it.copy(
                                    isReauthenticateWithEmailAndPasswordError = true,
                                    isLoading = false
                                )
                            }

                            when (result.error) {
                                FirebaseReauthenticate.FIREBASE_AUTH_INVALID_USER -> {

                                    _state.update {
                                        it.copy(
                                            errorSupportingText = "Invalid User Error: The current user's account has been disabled, deleted, or its credentials are no longer valid."
                                        )
                                    }

                                }

                                FirebaseReauthenticate.FIREBASE_AUTH_INVALID_CREDENTIALS -> {

                                    _state.update {
                                        it.copy(
                                            errorSupportingText = "The supplied credentials do not correspond to the previously signed in user."
                                        )
                                    }

                                }

                                FirebaseReauthenticate.FIREBASE_NETWORK -> {

                                    _state.update {
                                        it.copy(
                                            errorSupportingText = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                        )
                                    }

                                }

                                FirebaseReauthenticate.UNKNOWN -> {

                                    _state.update {
                                        it.copy(
                                            errorSupportingText = "Unknown error, please restart the app or try later."
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

    private suspend fun reauthenticateUser(
        navController: NavController,
        snackbar: SnackbarHostState
    ) {
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

                    FirebaseProviderId.GOOGLE -> {
                        when (
                            val reauthenticateWithGoogleResult =
                                firebase.reauthenticateWithGoogle()
                        ) {

                            is Result.Success -> {
                                navController.navigate(WelcomeScreen)
                            }

                            is Result.Error -> {
                                when (reauthenticateWithGoogleResult.error) {

                                    FirebaseReauthenticate.FIREBASE_AUTH_INVALID_USER -> {
                                        snackbar.showSnackbar(
                                            message = "Invalid User Error: The current user's account has been disabled, deleted, or its credentials are no longer valid."
                                        )
                                    }

                                    FirebaseReauthenticate.FIREBASE_AUTH_INVALID_CREDENTIALS -> {
                                        snackbar.showSnackbar(
                                            message = "The supplied credentials do not correspond to the previously signed in user."
                                        )
                                    }

                                    FirebaseReauthenticate.FIREBASE_NETWORK -> {
                                        snackbar.showSnackbar(
                                            message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                        )
                                    }

                                    FirebaseReauthenticate.UNKNOWN -> {
                                        snackbar.showSnackbar(
                                            message = "Unknown error, please restart the app or try later."
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

            }

            is Result.Error -> {

                when (providerIdResult.error) {
                    FirebaseCurrentUser.NULL_VALUE -> {
                        snackbar.showSnackbar(
                            message = "Error: Current User is null. If the error persists restart the or contact support."
                        )
                    }

                    FirebaseCurrentUser.UNEXPECTED_PROVIDER -> {
                        snackbar.showSnackbar(
                            message = "Error: Unexpected provider. Please restart the app and try again."
                        )
                    }

                    FirebaseCurrentUser.UNKNOWN -> {
                        snackbar.showSnackbar(
                            message = "Unknown error, please restart the app or try later."
                        )
                    }
                }
            }
        }
    }

}