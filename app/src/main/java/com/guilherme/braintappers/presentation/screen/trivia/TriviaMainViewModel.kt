package com.guilherme.braintappers.presentation.screen.trivia

import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import com.guilherme.braintappers.domain.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TriviaMainState(
    val questions: List<Question>? = null,
    val currentQuestion: Int = 0
)

sealed interface TriviaMainEvents {
    data object PreviousQuestion : TriviaMainEvents
    data object NextQuestion : TriviaMainEvents
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

        val lorem = trivia.fetchTriviaByCategory(
            numberOfQuestions = numberOfQuestions,
            categoryId = categoryId,
            difficulty = difficulty,
            type = type,
        )

        when (lorem) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        questions = lorem.data.results,
                    )
                }
            }

            is Result.Error -> {
                println("ERROR FETCHING DATA:" + lorem.error)
            }
        }

    }

    fun onEvent(event: TriviaMainEvents) {
        when (event) {
            TriviaMainEvents.PreviousQuestion -> {
                _state.update {
                    it.copy(
                        currentQuestion = _state.value.currentQuestion - 1
                    )
                }
            }

            TriviaMainEvents.NextQuestion -> {
                _state.update {
                    it.copy(
                        currentQuestion = _state.value.currentQuestion + 1
                    )
                }
            }
        }
    }

}