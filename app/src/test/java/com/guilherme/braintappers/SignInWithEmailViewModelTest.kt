package com.guilherme.braintappers

import androidx.navigation.NavHostController
import app.cash.turbine.test
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.FirebaseSignInWithEmailAndPasswordError
import com.guilherme.braintappers.domain.ResetPasswordError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.ResetPasswordState
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailEvents
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class SignInWithEmailViewModelTest : KoinTest {

    private val firebaseRepository: FirebaseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val viewModel: SignInWithEmailViewModel by inject()
    private val navController: NavHostController = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(module {
                single { firebaseRepository }
                factory { SignInWithEmailViewModel(get()) }
            })
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `when email text field changes, state should be updated`() = runTest {
        // Given
        val newEmail = "test@example.com"

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(newEmail))

        // Then
        viewModel.state.test {
            assertEquals(newEmail, awaitItem().emailTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when password text field changes, state should be updated`() = runTest {
        // Given
        val newPassword = "password123"

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(newPassword))

        // Then
        viewModel.state.test {
            assertEquals(newPassword, awaitItem().passwordTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when forgot password button is clicked, recover modal should be visible`() = runTest {
        // When
        viewModel.onEvent(SignInWithEmailEvents.OnForgotPasswordButtonClicked)

        // Then
        viewModel.state.test {
            assertTrue(awaitItem().recoverModalBottomSheetVisibility)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when recover modal dismissed, it should be hidden`() = runTest {
        // Given
        viewModel.onEvent(SignInWithEmailEvents.OnForgotPasswordButtonClicked)

        // When
        viewModel.onEvent(SignInWithEmailEvents.DismissRecoverModalBottomSheet)

        // Then
        viewModel.state.test {
            assertFalse(awaitItem().recoverModalBottomSheetVisibility)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when recover email text field changes, state should be updated`() = runTest {
        // Given
        val recoverEmail = "recover@example.com"

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnRecoverTextFieldChanged(recoverEmail))

        // Then
        viewModel.state.test {
            assertEquals(recoverEmail, awaitItem().recoverEmailTextField)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when sending reset password email succeeds, reset password state should be SUCCESS`() = runTest {
        // Given
        val recoverEmail = "recover@example.com"
        viewModel.onEvent(SignInWithEmailEvents.OnRecoverTextFieldChanged(recoverEmail))
        coEvery { firebaseRepository.resetPassword(recoverEmail) } returns Result.Success(Unit)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnSendResetPasswordEmailClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ResetPasswordState.SUCCESS, state.resetPasswordState)
            coVerify(exactly = 1) { firebaseRepository.resetPassword(recoverEmail) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when sending reset password email fails with network error, state should reflect error`() = runTest {
        // Given
        val recoverEmail = "recover@example.com"
        viewModel.onEvent(SignInWithEmailEvents.OnRecoverTextFieldChanged(recoverEmail))
        coEvery { firebaseRepository.resetPassword(recoverEmail) } returns Result.Error(ResetPasswordError.FIREBASE_NETWORK)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnSendResetPasswordEmailClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ResetPasswordState.ERROR, state.resetPasswordState)
            assertTrue(state.resetPasswordErrorMessage.contains("network error"))
            coVerify(exactly = 1) { firebaseRepository.resetPassword(recoverEmail) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when sending reset password email fails with unknown error, state should reflect error`() = runTest {
        // Given
        val recoverEmail = "recover@example.com"
        viewModel.onEvent(SignInWithEmailEvents.OnRecoverTextFieldChanged(recoverEmail))
        coEvery { firebaseRepository.resetPassword(recoverEmail) } returns Result.Error(ResetPasswordError.UNKNOWN)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnSendResetPasswordEmailClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ResetPasswordState.ERROR, state.resetPasswordState)
            assertTrue(state.resetPasswordErrorMessage.contains("Unknown error"))
            coVerify(exactly = 1) { firebaseRepository.resetPassword(recoverEmail) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signin succeeds, navigation should be called`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "password123"
        viewModel.onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(password))
        coEvery { firebaseRepository.signInWithEmail(email, password) } returns Result.Success(Unit)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test(timeout = 2.seconds) {
            val state = awaitItem()
            assertFalse(state.isLoading)
            verify(exactly = 1) { navController.navigate(HomeScreen) }
            coVerify(exactly = 1) { firebaseRepository.signInWithEmail(email, password) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signin fails with invalid credentials, state should reflect error`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "wrongPassword"
        viewModel.onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(password))
        coEvery { firebaseRepository.signInWithEmail(email, password) } returns
                Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            coVerify(exactly = 1) { firebaseRepository.signInWithEmail(email, password) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signin fails with network error, state should reflect error`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "password123"
        viewModel.onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(password))
        coEvery { firebaseRepository.signInWithEmail(email, password) } returns
                Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_NETWORK)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            coVerify(exactly = 1) { firebaseRepository.signInWithEmail(email, password) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when signin fails with unknown error, state should reflect error`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "password123"
        viewModel.onEvent(SignInWithEmailEvents.OnEmailTextFieldChanged(email))
        viewModel.onEvent(SignInWithEmailEvents.OnPasswordTextFieldChanged(password))
        coEvery { firebaseRepository.signInWithEmail(email, password) } returns
                Result.Error(FirebaseSignInWithEmailAndPasswordError.UNKNOWN)

        // When
        viewModel.onEvent(SignInWithEmailEvents.OnNextButtonClick(navController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            coVerify(exactly = 1) { firebaseRepository.signInWithEmail(email, password) }
            cancelAndConsumeRemainingEvents()
        }
    }
}