package com.guilherme.braintappers

import app.cash.turbine.test
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseGoogleAuthError
import com.guilherme.braintappers.domain.FirestoreError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.presentation.screen.trivia.TriviaMainEvents
import com.guilherme.braintappers.presentation.screen.trivia.TriviaMainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
class TriviaMainViewModelTest : KoinTest {

    private val testDispatcher = StandardTestDispatcher()
    private val triviaApiService: TriviaApiService = mockk()
    private val firebaseRepository: FirebaseFirestoreRepository = mockk()

    private val viewModel: TriviaMainViewModel by inject()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(module {
                single { triviaApiService }
                single { firebaseRepository }
                factory { TriviaMainViewModel(get(), get()) }
            })
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `fetchTrivia should update state with successful response`() = runTest {
        // Given
        val questions = listOf(
            Question(
                category = "Science",
                correctAnswer = "True",
                difficulty = "easy",
                incorrectAnswers = listOf("False"),
                question = "Is the Earth round?",
                type = "boolean"
            )
        )
        val apiResponse = ApiResponse(responseCode = 0, results = questions)
        val successResult: Result<ApiResponse, DataError> = Result.Success(apiResponse)

        coEvery { triviaApiService.fetchTriviaByCategory(any(), any(), any(), any()) } returns successResult

        // When
        viewModel.fetchTrivia("1", "9", "easy", "boolean")

        // Then
        viewModel.state.test {
            val finalState = awaitItem()
            assertEquals(successResult, finalState.result)
            assertEquals(questions, finalState.questions)
            assertEquals(9, finalState.categoryId)
            assertEquals(1, finalState.answers.size)
            assertFalse(finalState.isLoading)
            assertEquals(1, finalState.userAnswers.size)
            assertEquals("", finalState.userAnswers[0])
            cancelAndConsumeRemainingEvents()
        }

        coVerify { triviaApiService.fetchTriviaByCategory("1", "9", "easy", "boolean") }
    }

    @Test
    fun `fetchTrivia should update state with error response`() = runTest {
        // Given
        val errorResult = Result.Error<ApiResponse, DataError>(DataError.NO_INTERNET)

        coEvery { triviaApiService.fetchTriviaByCategory(any(), any(), any(), any()) } returns errorResult

        // When
        viewModel.fetchTrivia("1", "9", "easy", "boolean")

        // Then
        viewModel.state.test {
            val finalState = awaitItem()
            assertEquals(errorResult, finalState.result)
            assertFalse(finalState.isLoading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent PreviousQuestion should decrement questionIndex when not at beginning`() = runTest {
        // Given
        // Set initial state with questionIndex > 0
        setInitialStateWithQuestions(questionIndex = 1)

        // When
        viewModel.onEvent(TriviaMainEvents.PreviousQuestion)

        // Then
        viewModel.state.test {
            assertEquals(0, awaitItem().questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent PreviousQuestion should not decrement questionIndex when at beginning`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 0)

        // When
        viewModel.onEvent(TriviaMainEvents.PreviousQuestion)

        // Then
        viewModel.state.test {
            assertEquals(0, awaitItem().questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent NextQuestion should increment questionIndex when not at end`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 0)

        // When
        viewModel.onEvent(TriviaMainEvents.NextQuestion)

        // Then
        viewModel.state.test {
            assertEquals(1, awaitItem().questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent NextQuestion should not increment questionIndex when at end`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 1)

        // When
        viewModel.onEvent(TriviaMainEvents.NextQuestion)

        // Then
        viewModel.state.test {
            assertEquals(1, awaitItem().questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent OnAnswerClicked should update userAnswers at current index`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 0)

        // When
        viewModel.onEvent(TriviaMainEvents.OnAnswerClicked("True"))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("True", state.userAnswers[0])
            // Check that it auto-advances to next question
            assertEquals(1, state.questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent OnAnswerClicked should not increment questionIndex when at last question`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 1)

        // When
        viewModel.onEvent(TriviaMainEvents.OnAnswerClicked("False"))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("False", state.userAnswers[1])
            assertEquals(1, state.questionIndex) // Should remain at index 1
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent NavigateToQuestion should update questionIndex`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 0)

        // When
        viewModel.onEvent(TriviaMainEvents.NavigateToQuestion(1))

        // Then
        viewModel.state.test {
            assertEquals(1, awaitItem().questionIndex)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearSnackBar should set snackBarMessage to null`() = runTest {
        // Given
        setInitialStateWithQuestions(questionIndex = 0)
        val error = Result.Error<Unit, FirestoreError>(FirestoreError.FIREBASE_NETWORK)
        coEvery { firebaseRepository.write(any(), any()) } returns error
        viewModel.onEvent(TriviaMainEvents.OnFinishButtonClicked) // Set snackbar message

        // When
        viewModel.clearSnackBar()

        // Then
        viewModel.state.test {
            assertNull(awaitItem().snackBarMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    /**
     * Helper method to set initial state with test questions
     */
    private fun setInitialStateWithQuestions(questionIndex: Int) = runTest {
        val questions = listOf(
            Question(
                category = "Science",
                correctAnswer = "True",
                difficulty = "easy",
                incorrectAnswers = listOf("False"),
                question = "Is the Earth round?",
                type = "boolean"
            ),
            Question(
                category = "Geography",
                correctAnswer = "Paris",
                difficulty = "easy",
                incorrectAnswers = listOf("London", "Berlin", "Rome"),
                question = "What is the capital of France?",
                type = "multiple"
            )
        )

        val apiResponse = ApiResponse(responseCode = 0, results = questions)
        val successResult: Result<ApiResponse, DataError>  = Result.Success(apiResponse)

        coEvery { triviaApiService.fetchTriviaByCategory(any(), any(), any(), any()) } returns successResult

        viewModel.fetchTrivia("2", "9", "easy", "multiple")

        // Manually set the question index for testing different scenarios
        if (questionIndex > 0) {
            viewModel.onEvent(TriviaMainEvents.NavigateToQuestion(questionIndex))
        }
    }
}