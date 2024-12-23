package com.guilherme.braintappers.presentation.screen.profile

import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.domain.FirebaseRepository

data class ProfileState(
    val user: String = ""
)

class ProfileViewModel(private val firebase: FirebaseRepository) : ViewModel() {

}