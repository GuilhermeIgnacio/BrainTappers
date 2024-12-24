package com.guilherme.braintappers.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.navigation.WelcomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait

data class ProfileState(
    val user: FirebaseUser? = null,
)

sealed interface ProfileEvents {
    data class OnConfirmSignOut(val value: NavController) : ProfileEvents
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
        }
    }

}