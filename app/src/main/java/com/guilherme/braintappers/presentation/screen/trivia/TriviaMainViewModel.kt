package com.guilherme.braintappers.presentation.screen.trivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.navigation.HomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TriviaMainState(
    val questions: List<Question>? = null,
    val result: Result<ApiResponse, DataError>? = null,
)

sealed interface TriviaMainEvents {

}

class TriviaMainViewModel(
    private val trivia: TriviaApiService
) : ViewModel() {

    private val _state = MutableStateFlow(TriviaMainState())
    val state = _state.asStateFlow()

    suspend fun fetchTrivia(
        numberOfQuestions: String,
        categoryId: String,
        difficulty: String,
        type: String
    ) {

        val trivia = trivia.fetchTriviaByCategory(
            numberOfQuestions = numberOfQuestions,
            categoryId = categoryId,
            difficulty = difficulty,
            type = type,
        )

        _state.update {
            it.copy(
                result = trivia
            )
        }

        when (trivia) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        questions = trivia.data.results,
                    )
                }
            }

            is Result.Error -> {
                println("ERROR FETCHING DATA:" + trivia.error)
            }
        }

    }

    fun onEvent(event: TriviaMainEvents) {
        when (event) {

            else -> {}
        }
    }

}