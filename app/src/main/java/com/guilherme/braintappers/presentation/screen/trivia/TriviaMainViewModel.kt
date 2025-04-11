package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirestoreError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.items
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

data class TriviaMainState(
    val result: Result<ApiResponse, DataError>? = null,
    val questionIndex: Int = 0,
    val answers: List<List<String>> = emptyList(),
    val questions: List<Question> = emptyList(),
    val userAnswers: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isTriviaFinished: Boolean = false,
    val categoryId: Int = 0,
    val snackBarMessage: String? = null
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

        _state.update { it.copy(isLoading = true) }

        val trivia = trivia.fetchTriviaByCategory(
            numberOfQuestions = numberOfQuestions,
            categoryId = categoryId,
            difficulty = difficulty,
            type = type,
        )

        _state.update { it.copy(result = trivia) }

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
                        categoryId = categoryId.toInt(),
                        isLoading = false
                    )
                }
            }

            is Result.Error -> {
                println("ERROR FETCHING DATA:" + trivia.error)
                _state.update { it.copy(isLoading = false) }
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
                        "correctAnswers" to correctAnswers,
                        "category" to items.find { it.categoryId == _state.value.categoryId }?.contentDescription,
                        "createdAt" to Timestamp(Date())
                    )



                    when (val result = firestore.write(quizUid = uuid, data = data)) {
                        is Result.Success -> {
                            _state.update { it.copy(isLoading = false, isTriviaFinished = true) }
                        }

                        is Result.Error -> {

                            val errorMessage = when (result.error) {
                                FirestoreError.FIREBASE_NETWORK -> "A network error (such as timeout, interrupted connection or unreachable host) has occurred"

                                FirestoreError.UNKNOWN -> "Unknown error, please restart the app or try later."
                            }

                            _state.update { it.copy(isLoading = false, snackBarMessage = errorMessage) }

                        }
                    }


                }
            }
        }
    }

    fun clearSnackBar() {
        _state.update { it.copy(snackBarMessage = null) }
    }

}