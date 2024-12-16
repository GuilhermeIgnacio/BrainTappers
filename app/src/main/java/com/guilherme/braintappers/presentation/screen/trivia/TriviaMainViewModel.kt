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
    val result: Result<ApiResponse, DataError>? = null,
    val questionIndex: Int = 0,
    val answers: List<List<String>> = emptyList(),
    val questions: List<Question> = emptyList(),
    val userAnswers: List<String> = emptyList()
)

sealed interface TriviaMainEvents {
    data object PreviousQuestion : TriviaMainEvents
    data object NextQuestion : TriviaMainEvents
    data class OnAnswerClicked(val value: String) : TriviaMainEvents
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

                val answers = trivia.data.results.map { it.incorrectAnswers + it.correctAnswer }
                    .toMutableList()
                answers.map { it.shuffled() }

                _state.update {
                    it.copy(
                        answers = answers,
                        userAnswers = MutableList(answers.size) { "" },
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
            TriviaMainEvents.PreviousQuestion -> {
                if (_state.value.questionIndex > 0) {
                    _state.update {
                        it.copy(
                            questionIndex = _state.value.questionIndex - 1
                        )
                    }
                }
            }

            TriviaMainEvents.NextQuestion -> {
                if (_state.value.questionIndex != _state.value.questions.size - 1) {
                    _state.update {
                        it.copy(
                            questionIndex = _state.value.questionIndex + 1
                        )
                    }
                }
            }

            is TriviaMainEvents.OnAnswerClicked -> {

                val userAnswers = _state.value.userAnswers.toMutableList()
                val questionIndex = _state.value.questionIndex

                userAnswers[questionIndex] = event.value

                _state.update { it.copy(
                    userAnswers = userAnswers
                ) }

            }
        }
    }

}