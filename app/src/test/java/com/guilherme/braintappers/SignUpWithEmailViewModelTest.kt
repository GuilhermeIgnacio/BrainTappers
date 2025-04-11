package com.guilherme.braintappers

import androidx.navigation.NavHostController
import app.cash.turbine.test
import com.guilherme.braintappers.domain.FirebaseEmailAndPasswordAuthError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailEvents
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class SignUpWithEmailViewModelTest : KoinTest {

    private val firebaseRepository: FirebaseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val viewModel: SignUpWithEmailViewModel by inject()
    private val navController: NavHostController = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(module {
                single { firebaseRepository }
                factory { SignUpWithEmailViewModel(get()) }
            })
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `when email text field changes, state should be updated and converted to lowercase`() = runTest {
        // Given
        val inputEmail = "TEST@Example.COM"
        val expectedEmail = "test@example.com"

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(inputEmail))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(expectedEmail, awaitItem().emailTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when confirm email text field changes, state should be updated and converted to lowercase`() = runTest {
        // Given
        val inputEmail = "TEST@Example.COM"
        val expectedEmail = "test@example.com"

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnConfirmEmailTextFieldChanged(inputEmail))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(expectedEmail, awaitItem().confirmEmailTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when password text field changes, state should be updated`() = runTest {
        // Given
        val password = "Password123!"

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(password, awaitItem().passwordTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when confirm password text field changes, state should be updated`() = runTest {
        // Given
        val confirmPassword = "Password123!"

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnConfirmPasswordTextFieldChanged(confirmPassword))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertEquals(confirmPassword, awaitItem().confirmPasswordTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signup succeeds, navigation should be called and loading state should be updated`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Password123!"

        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { firebaseRepository.signUpWithEmail(email, password) } returns Result.Success(Unit)

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test(timeout = 2.seconds) {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.snackBarMessage)

            verify(exactly = 1) { navController.navigate(HomeScreen) }
            coVerify(exactly = 1) { firebaseRepository.signUpWithEmail(email, password) }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signup fails with user collision error, state should reflect error`() = runTest {
        // Given
        val email = "existing@example.com"
        val password = "Password123!"

        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { firebaseRepository.signUpWithEmail(email, password) } returns
                Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_AUTH_USER_COLLISION)

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.snackBarMessage?.contains("already in use") == true)

//            verify(exactly = 0) { navController.navigate(any()) }
            coVerify(exactly = 1) { firebaseRepository.signUpWithEmail(email, password) }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signup fails with network error, state should reflect error`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Password123!"

        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { firebaseRepository.signUpWithEmail(email, password) } returns
                Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_NETWORK)

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.snackBarMessage?.contains("network error") == true)

//            verify(exactly = 0) { navController.navigate(any()) }
            coVerify(exactly = 1) { firebaseRepository.signUpWithEmail(email, password) }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signup fails with unknown error, state should reflect error`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Password123!"

        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { firebaseRepository.signUpWithEmail(email, password) } returns
                Result.Error(FirebaseEmailAndPasswordAuthError.UNKNOWN)

        // When
        viewModel.onEvent(SignUpWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.snackBarMessage?.contains("Unknown error") == true)

//            verify(exactly = 0) { navController.navigate(any()) }
            coVerify(exactly = 1) { firebaseRepository.signUpWithEmail(email, password) }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when clearSnackBar is called, snackBarMessage should be null`() = runTest {
        // Given - Set up a state with a snackbar message
        val email = "user@example.com"
        val password = "Password123!"

        viewModel.onEvent(SignUpWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignUpWithEmailEvents.OnPasswordTextFieldChanged(password))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { firebaseRepository.signUpWithEmail(email, password) } returns
                Result.Error(FirebaseEmailAndPasswordAuthError.UNKNOWN)

        viewModel.onEvent(SignUpWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify snackbar message exists
        viewModel.state.test {
            assertTrue(awaitItem().snackBarMessage != null)
            cancelAndConsumeRemainingEvents()
        }

        // When
        viewModel.clearSnackBar()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            assertNull(awaitItem().snackBarMessage)
            cancelAndConsumeRemainingEvents()
        }
    }
}