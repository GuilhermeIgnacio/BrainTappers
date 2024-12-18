package com.guilherme.braintappers.presentation.screen.welcome

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseException
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelcomeState(
    val isLoading: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)

sealed interface WelcomeEvents {
    data class OnContinueAnonymously(val value: NavHostController) : WelcomeEvents
}

class WelcomeScreenViewModel(
    private val firebase: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeState())
    val state = _state.asStateFlow()

    fun onEvent(event: WelcomeEvents) {
        when (event) {
            is WelcomeEvents.OnContinueAnonymously -> {
                viewModelScope.launch {

                    updateLoadingState(true)

                    try {
                        firebase.createAnonymousAccount()
                    } catch (e: FirebaseException) {
                        showErrorMessage(e)
                    }

                    try {
                        if (firebase.currentUser() != null) {
                            event.value.navigate(HomeScreen)
                        }
                    } catch (e: FirebaseException) {
                        showErrorMessage(e)
                    }

                    updateLoadingState(false)

                }
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private suspend fun showErrorMessage(e: FirebaseException) {
        _state.update {
            it.copy(
                isLoading = false
            )
        }

        _state.value.snackbarHostState.showSnackbar(
            message = "${e.message}",
            actionLabel = "Close",
            duration = SnackbarDuration.Long
        )
    }

}