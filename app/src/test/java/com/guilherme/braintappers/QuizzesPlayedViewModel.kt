package com.guilherme.braintappers

import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.turbine.test
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseGetUserQuizzes
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.model.QuizResults
import com.guilherme.braintappers.presentation.screen.quizzesplayed.QuizzesPlayedViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class QuizzesPlayedViewModel : KoinTest {

    private val firestoreRepository = mockk<FirebaseFirestoreRepository>()
    private val mockQuizResults = mockk<QuizResults>()

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUserPlayedQuizzes success`() = runTest {
        coEvery { firestoreRepository.getUserPlayedQuizzes() } returns Result.Success(
            listOf(
                mockQuizResults
            )
        )
        val viewModel = QuizzesPlayedViewModel(firestoreRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(state.quizResults, listOf(mockQuizResults))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getUserPlayedQuizzes error`() = runTest {
        coEvery { firestoreRepository.getUserPlayedQuizzes() } returns Result.Error(FirebaseGetUserQuizzes.UNKNOWN)
        testDispatcher.scheduler.advanceUntilIdle()

        val viewModel = QuizzesPlayedViewModel(firestoreRepository)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(state.quizResults, null)
        }
    }

}