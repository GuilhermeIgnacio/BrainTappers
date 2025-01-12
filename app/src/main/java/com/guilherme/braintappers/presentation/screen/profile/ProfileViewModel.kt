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
import com.guilherme.braintappers.domain.LinkAccountWithEmailError
import com.guilherme.braintappers.domain.LinkAccountWithGoogleError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.WelcomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: FirebaseUser? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val profileModalBottomSheetState: ProfileModalBottomSheetState = ProfileModalBottomSheetState.INACTIVE,
    val profileModalBottomSheetErrorMessage: String = "",
    val emailTextField: String = "",
    val confirmEmailTextField: String = "",
    val passwordTextField: String = "",
    val confirmPasswordTextField: String = "",
    val isReauthenticateWithEmailAndPasswordError: Boolean = false,
    val errorSupportingText: String = "",
    val isLoading: Boolean = false,
    val isAnonymousUser: Boolean = false
)

sealed interface ProfileEvents {
    data class OnConfirmSignOut(val value: NavController) : ProfileEvents
    data class OnConfirmAccountDeletion(val value: NavController) : ProfileEvents
    data object OnConfirmClearHistory : ProfileEvents
    data class OnEmailTextFieldValueChanged(val value: String) : ProfileEvents
    data class OnConfirmEmailTextFieldValueChanged(val value: String) : ProfileEvents
    data class OnPasswordChanged(val value: String) : ProfileEvents
    data class OnConfirmPasswordTextFieldValueChanged(val value: String) : ProfileEvents
    data object DismissModalBottomSheet : ProfileEvents

    data class ReauthenticateWithEmailAndPassword(val value: NavController) : ProfileEvents

    data object OpenLinkAccountWithEmailModalBottomSheet : ProfileEvents
    data object LinkAccount : ProfileEvents
    data object LinkAccountWithGoogle : ProfileEvents
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
                    user = firebase.currentUser(),
                    isAnonymousUser = firebase.currentUser()?.isAnonymous ?: true
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

            is ProfileEvents.OnConfirmEmailTextFieldValueChanged -> {
                _state.update {
                    it.copy(
                        confirmEmailTextField = event.value
                    )
                }
            }

            is ProfileEvents.OnPasswordChanged -> {
                _state.update { it.copy(passwordTextField = event.value) }
            }

            is ProfileEvents.OnConfirmPasswordTextFieldValueChanged -> {
                _state.update { it.copy(confirmPasswordTextField = event.value) }
            }

            ProfileEvents.DismissModalBottomSheet -> {
                _state.update {
                    it.copy(
                        profileModalBottomSheetState = ProfileModalBottomSheetState.INACTIVE,
                        emailTextField = "",
                        confirmEmailTextField = "",
                        passwordTextField = "",
                        confirmPasswordTextField = "",
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
                                    profileModalBottomSheetState = ProfileModalBottomSheetState.INACTIVE,
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

                                FirebaseReauthenticate.GET_CREDENTIAL -> {
                                    _state.update { it.copy(
                                        errorSupportingText = "Get Credential Error."
                                    ) }
                                }

                                FirebaseReauthenticate.GET_CREDENTIAL_CANCELLATION -> {
                                    _state.update { it.copy(
                                        errorSupportingText = "Operation cancelled by user."
                                    ) }
                                }
                            }
                        }
                    }
                }
            }

            ProfileEvents.OpenLinkAccountWithEmailModalBottomSheet -> {
                _state.update { it.copy(profileModalBottomSheetState = ProfileModalBottomSheetState.LINK_ANONYMOUS_USER_WITH_EMAIL) }
            }

            ProfileEvents.LinkAccount -> {
                val email = _state.value.emailTextField
                val password = _state.value.passwordTextField

                viewModelScope.launch {
                    when (val result = firebase.linkAccountWithEmail(email, password)) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    profileModalBottomSheetState = ProfileModalBottomSheetState.SUCCESS,
                                    isAnonymousUser = false,
                                )
                            }
                        }

                        is Result.Error -> {

                            _state.update { it.copy(profileModalBottomSheetState = ProfileModalBottomSheetState.ERROR) }

                            when (result.error) {
                                LinkAccountWithEmailError.FIREBASE_AUTH_WEAK_PASSWORD -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "Password is not strong enough."
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.FIREBASE_AUTH_INVALID_CREDENTIALS -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "The credential is malformed or expired."
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.FIREBASE_AUTH_USER_COLLISION -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "The email address you entered is already associated with an account. Please try use a different email address."
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.FIREBASE_AUTH_INVALID_USER -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "The user credentials are no longer valid, please try using a different account."
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.FIREBASE_AUTH -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "This provider is already linked to this credentials."
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.FIREBASE_NETWORK -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                        )
                                    }
                                }

                                LinkAccountWithEmailError.UNKNOWN -> {
                                    _state.update {
                                        it.copy(
                                            profileModalBottomSheetErrorMessage = "Unknown error, please restart the app or try later."
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }

            ProfileEvents.LinkAccountWithGoogle -> {

                viewModelScope.launch {

                    _state.update { it.copy(isLoading = true) }

                    val snackbar = _state.value.snackbarHostState
                    when (val result = firebase.linkAccountWithGoogle()) {

                        is Result.Success -> {

                            _state.update { it.copy(isAnonymousUser = false, isLoading = false) }
                            snackbar.showSnackbar(
                                message = "User Successfully Linked To Google Account."
                            )

                        }

                        is Result.Error -> {

                            _state.update { it.copy(isLoading = false) }

                            when (result.error) {
                                LinkAccountWithGoogleError.FIREBASE_AUTH_WEAK_PASSWORD -> {
                                    snackbar.showSnackbar(
                                        message = "Password is not strong enough."
                                    )
                                }

                                LinkAccountWithGoogleError.FIREBASE_AUTH_INVALID_CREDENTIALS -> {
                                    snackbar.showSnackbar(
                                        message = "The credential is malformed or expired."
                                    )
                                }

                                LinkAccountWithGoogleError.FIREBASE_AUTH_USER_COLLISION -> {
                                    snackbar.showSnackbar(
                                        message = "The selected Google account is already in use. Please try using a different one."
                                    )
                                }

                                LinkAccountWithGoogleError.FIREBASE_AUTH_INVALID_USER -> {
                                    snackbar.showSnackbar(
                                        message = "Invalid User Error: The current user's account has been disabled, deleted, or its credentials are no longer valid."
                                    )
                                }

                                LinkAccountWithGoogleError.FIREBASE_AUTH -> {
                                    snackbar.showSnackbar(
                                        message = "This provider is already linked to this credentials."
                                    )
                                }
                                LinkAccountWithGoogleError.FIREBASE_NETWORK -> {
                                    snackbar.showSnackbar(
                                        message = "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                                    )
                                }
                                LinkAccountWithGoogleError.GET_CREDENTIAL -> {
                                    snackbar.showSnackbar(
                                        message = "Unknown error, please restart the app or try later."
                                    )
                                }

                                LinkAccountWithGoogleError.GET_CREDENTIAL_CANCELLATION -> {
                                    snackbar.showSnackbar(
                                        message = "Operation cancelled by user."
                                    )
                                }

                                LinkAccountWithGoogleError.UNKNOWN -> {
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
                                profileModalBottomSheetState = ProfileModalBottomSheetState.AUTHENTICATE_WITH_EMAIL
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

                                    FirebaseReauthenticate.GET_CREDENTIAL -> {
                                        snackbar.showSnackbar(
                                            message = "Get Credential Error."
                                        )
                                    }

                                    FirebaseReauthenticate.GET_CREDENTIAL_CANCELLATION -> {
                                        snackbar.showSnackbar(
                                            message = "Operation cancelled by user."
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

enum class ProfileModalBottomSheetState {
    INACTIVE,
    AUTHENTICATE_WITH_EMAIL,
    LINK_ANONYMOUS_USER_WITH_EMAIL,
    SUCCESS,
    ERROR
}