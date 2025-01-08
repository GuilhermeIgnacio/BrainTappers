package com.guilherme.braintappers.presentation.screen.quizzesplayed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.domain.model.QuizResults
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizzesPlayedState(
    val quizResults: List<QuizResults> = emptyList()
)

class QuizzesPlayedViewModel(
    private val firestore: FirebaseFirestoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizzesPlayedState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = firestore.getUserPlayedQuizzes()) {
                is Result.Success -> {
                    println("Played Quiz Operations Success")
                    _state.update {
                        it.copy(
                            quizResults = result.data
                        )
                    }
                }

                is Result.Error -> {
                    println("Played Quiz Operations Error")
                }
            }

        }
    }

}