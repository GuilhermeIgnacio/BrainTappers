package com.guilherme.braintappers.presentation.screen.trivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TriviaMainState(
    val questions: List<Question>? = null,
    val currentQuestion: Int = 0,
    val answeredQuestions: MutableList<UserTrivia> = mutableListOf(),
    val selectedAnswer: List<String> = mutableListOf("", "", "", "", "", "", "", "", "", "")
)

sealed interface TriviaMainEvents {
    data class OnAnswerClicked(val value: UserTrivia) : TriviaMainEvents
    data object PreviousQuestion : TriviaMainEvents
    data object NextQuestion : TriviaMainEvents
    data object FinishTrivia: TriviaMainEvents
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

            is TriviaMainEvents.OnAnswerClicked -> {


                val answeredQuestionsList = _state.value.answeredQuestions
                val selectedAnswersList = _state.value.selectedAnswer.toMutableList()

                selectedAnswersList[_state.value.currentQuestion] =
                    event.value.selectedAnswer

                val existingIndex =
                    answeredQuestionsList.indexOfFirst { it.question == event.value.question }

                if (existingIndex != -1) {
                    // If exists, overwrite for the new one
                    answeredQuestionsList[existingIndex] = event.value
                } else {
                    // If does not exists, add new
                    answeredQuestionsList.add(event.value)
                }

                _state.update {
                    it.copy(
                        selectedAnswer = selectedAnswersList.toList()
                    )
                }

            }

            TriviaMainEvents.FinishTrivia -> {
                
            }
        }
    }

}