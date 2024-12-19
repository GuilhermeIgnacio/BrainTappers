package com.guilherme.braintappers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.navigation.WelcomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val isLoading: Boolean = true,
    val startDestination: Any? = null
)

class MainViewModel(
    private val firebase: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val startDestination = if (firebase.currentUser() == null) WelcomeScreen else HomeScreen
            _state.update { it.copy(
                startDestination = startDestination,
                isLoading = false
            ) }
        }
    }

}