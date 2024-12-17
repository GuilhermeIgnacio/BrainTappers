package com.guilherme.braintappers.presentation.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseException
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed interface WelcomeEvents {
    data class OnContinueAnonymously(val value: NavHostController) : WelcomeEvents
}

class WelcomeScreenViewModel(
    private val firebase: FirebaseRepository
) : ViewModel() {

    fun onEvent(event: WelcomeEvents) {
        when (event) {
            is WelcomeEvents.OnContinueAnonymously -> {
                viewModelScope.launch {
                    try {
                        firebase.createAnonymousAccount()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        if (firebase.currentUser() != null) {
                            event.value.navigate(HomeScreen)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

}