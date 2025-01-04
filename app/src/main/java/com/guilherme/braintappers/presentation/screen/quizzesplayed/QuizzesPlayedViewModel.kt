package com.guilherme.braintappers.presentation.screen.quizzesplayed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import kotlinx.coroutines.launch

class QuizzesPlayedViewModel(
    private val firestore: FirebaseFirestoreRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            firestore.getUserPlayedQuizzes()
        }
    }

}