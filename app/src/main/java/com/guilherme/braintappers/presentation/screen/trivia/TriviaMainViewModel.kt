package com.guilherme.braintappers.presentation.screen.trivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
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
import java.util.UUID
import kotlin.uuid.Uuid

data class TriviaMainState(
    val result: Result<ApiResponse, DataError>? = null,
    val questionIndex: Int = 0,
    val answers: List<List<String>> = emptyList(),
    val questions: List<Question> = emptyList(),
    val userAnswers: List<String> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface TriviaMainEvents {
    data object PreviousQuestion : TriviaMainEvents
    data object NextQuestion : TriviaMainEvents
    data class OnAnswerClicked(val value: String) : TriviaMainEvents
    data class NavigateToQuestion(val value: Int) : TriviaMainEvents
    data object OnFinishButtonClicked : TriviaMainEvents
}

class TriviaMainViewModel(
    private val trivia: TriviaApiService,
    private val firestore: FirebaseFirestoreRepository
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
                val questions = _state.value.questions

                userAnswers[questionIndex] = event.value

                _state.update {
                    it.copy(
                        userAnswers = userAnswers,
                        questionIndex = if (questions.size - 1 != questionIndex) (questionIndex + 1) else questionIndex
                    )
                }

            }

            is TriviaMainEvents.NavigateToQuestion -> {
                _state.update {
                    it.copy(
                        questionIndex = event.value
                    )
                }
            }

            TriviaMainEvents.OnFinishButtonClicked -> {
                viewModelScope.launch {

                    _state.update { it.copy(isLoading = true) }

                    val uuid = UUID.randomUUID().toString()

                    val questions = _state.value.questions.map { it.question }
                    val userAnswers = _state.value.userAnswers
                    val correctAnswers = _state.value.questions.map { it.correctAnswer }

                    val data = hashMapOf(
                        "questions" to questions,
                        "userAnswers" to userAnswers,
                        "correctAnswers" to correctAnswers
                    )

                    when (val result = firestore.write(quizUid = uuid, data = data)) {
                        is Result.Success -> {
                            _state.update { it.copy(isLoading = false) }

                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false) }

                        }
                    }

                }
            }
        }
    }

}