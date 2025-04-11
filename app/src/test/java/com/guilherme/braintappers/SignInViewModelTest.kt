package com.guilherme.braintappers

import androidx.navigation.NavHostController
import app.cash.turbine.test
import com.guilherme.braintappers.domain.FirebaseGoogleAuthError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.presentation.screen.signin.SignInEvents
import com.guilherme.braintappers.presentation.screen.signin.SignInViewModel
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
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest : KoinTest {

    private val firebaseRepository = mockk<FirebaseRepository>()
    private val mockNavHostController = mockk<NavHostController>(relaxed = true)
    private val viewModel: SignInViewModel by inject()

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(
                module {
                    single { firebaseRepository }
                    viewModel { SignInViewModel(get()) }
                }
            )
        }

    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `OnSignInWithGoogleClick returns success`() = runTest {

        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Success(Unit)

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertEquals(initialState.snackBarMessage.isNullOrEmpty(), true)

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))

            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)

            coVerify { firebaseRepository.signUpWithGoogle() }
            verify { mockNavHostController.navigate(HomeScreen) }

            cancelAndIgnoreRemainingEvents()
        }


    }

    @Test
    fun `OnSignInWithGoogleClick returns FIREBASE_AUTH_INVALID_USER`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_USER
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())
            assertFalse(initialState.isLoading)

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))

            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)


            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(finalState.snackBarMessage, "Error: Invalid User")
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns FIREBASE_AUTH_INVALID_CREDENTIALS`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_CREDENTIALS
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(finalState.snackBarMessage, "Error: Invalid Credentials")
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns FIREBASE_AUTH_USER_COLLISION`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.FIREBASE_AUTH_USER_COLLISION
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(
                finalState.snackBarMessage,
                "Error: there already exists an account with the email address asserted by the credential."
            )
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns FIREBASE_NETWORK`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.FIREBASE_NETWORK
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(
                finalState.snackBarMessage,
                "A network error (such as timeout, interrupted connection or unreachable host) has occurred."
            )
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns GET_CREDENTIAL`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.GET_CREDENTIAL
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(finalState.snackBarMessage, "Get Credential Error")
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns GET_CREDENTIAL_CANCELLATION`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.GET_CREDENTIAL_CANCELLATION
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(finalState.snackBarMessage, "Operation cancelled by user.")
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns NO_CREDENTIAL`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.NO_CREDENTIAL
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(
                finalState.snackBarMessage,
                "No Google accounts found on this device. Please add a Google account to proceed."
            )
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `OnSignInWithGoogleClick returns UNKNOWN`() = runTest {
        coEvery { firebaseRepository.signUpWithGoogle() } returns Result.Error(
            FirebaseGoogleAuthError.UNKNOWN
        )

        viewModel.state.test {

            val initialState = awaitItem()
            assertFalse (initialState.isLoading)
            assertTrue(initialState.snackBarMessage.isNullOrEmpty())

            val onEvent = viewModel::onEvent
            onEvent(SignInEvents.OnSignInWithGoogleClick(mockNavHostController))
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertEquals(
                finalState.snackBarMessage,
                "Unknown error, please restart the app or try later."
            )
            cancelAndIgnoreRemainingEvents()
        }

    }

}