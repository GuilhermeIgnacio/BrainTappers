package com.guilherme.braintappers

import androidx.navigation.NavController
import app.cash.turbine.test
import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.data.FirebaseProviderId
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseReauthenticate
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.LinkAccountWithEmailError
import com.guilherme.braintappers.domain.LinkAccountWithGoogleError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.navigation.WelcomeScreen
import com.guilherme.braintappers.presentation.screen.profile.ProfileEvents
import com.guilherme.braintappers.presentation.screen.profile.ProfileModalBottomSheetState
import com.guilherme.braintappers.presentation.screen.profile.ProfileViewModel
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
class ProfileViewModelTest : KoinTest {

    private val firebaseRepository = mockk<FirebaseRepository>()
    private val firestoreRepository = mockk<FirebaseFirestoreRepository>()
    private val mockUser = mockk<FirebaseUser>(relaxed = true)
    private val mockNavController = mockk<NavController>(relaxed = true)
    private val viewModel: ProfileViewModel by inject()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(
                module {
                    single { firebaseRepository }
                    single { firestoreRepository }
                    viewModel { ProfileViewModel(get(), get()) }
                }

            )
        }
        coEvery { firebaseRepository.currentUser() } returns mockUser

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
        coEvery { firebaseRepository.currentUser() } returns null
    }

    @Test
    fun `init should set user and set anonymousUser to false`() = runTest {
        coEvery { firebaseRepository.currentUser() } returns mockUser
        coEvery { mockUser.isAnonymous } returns false

        val viewModel = ProfileViewModel(firebaseRepository, firestoreRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(mockUser, state.user)
            assertFalse { state.isAnonymousUser }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should set user and set anonymousUser to true`() = runTest {

        coEvery { firebaseRepository.currentUser() } returns mockUser
        coEvery { mockUser.isAnonymous } returns true

        val viewModel = ProfileViewModel(firebaseRepository, firestoreRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(mockUser, state.user)
            assertTrue { state.isAnonymousUser }
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `onConfirmSignOut should sign out and navigate to welcome screen`() = runTest {

        coEvery { firebaseRepository.currentUser() } returns mockUser
        coEvery { firebaseRepository.signOut() } returns Unit


        val onEvent = viewModel::onEvent
        onEvent(ProfileEvents.OnConfirmSignOut(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { firebaseRepository.signOut() }
        verify { mockNavController.navigate(WelcomeScreen) }

    }

    @Test
    fun `onConfirmClearHistory should show success snackbar on success`() = runTest {
        coEvery { firebaseRepository.currentUser() } returns mockUser
        coEvery { firestoreRepository.deleteData() } returns Result.Success(Unit)

        val onEvent = viewModel::onEvent
        onEvent(ProfileEvents.OnConfirmClearHistory)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { firestoreRepository.deleteData() }

    }

    @Test
    fun `onEmailTextFieldValueChanged should update email value`() = runTest {

        coEvery { firebaseRepository.currentUser() } returns mockUser

        val onEvent = viewModel::onEvent
        onEvent(ProfileEvents.OnEmailTextFieldValueChanged("test@test.com"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(state.emailTextField, "test@test.com")
        }

    }

    @Test
    fun `dismissModalBottomSheet should reset relevant fields`() = runTest {
        coEvery { firebaseRepository.currentUser() } returns mockUser

        val onEvent = viewModel::onEvent
        onEvent(ProfileEvents.OnEmailTextFieldValueChanged("email@email.com"))
        onEvent(ProfileEvents.OnConfirmEmailTextFieldValueChanged("email@email.com"))
        onEvent(ProfileEvents.OnPasswordChanged("password"))
        onEvent(ProfileEvents.OnConfirmPasswordTextFieldValueChanged("password"))

        onEvent(ProfileEvents.DismissModalBottomSheet)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(state.emailTextField,"")
            assertEquals(state.confirmEmailTextField,"")
            assertEquals(state.passwordTextField,"")
            assertEquals(state.confirmPasswordTextField,"")
        }

    }

    @Test
    fun `onConfirmAccountDeletion success should navigate to welcome screen`() = runTest {
        coEvery { firebaseRepository.currentUser() } returns mockUser
        coEvery { firebaseRepository.deleteAccount() } returns Result.Success(Unit)

        // Act
        viewModel.onEvent(ProfileEvents.OnConfirmAccountDeletion(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { firebaseRepository.deleteAccount() }
        verify { mockNavController.navigate(WelcomeScreen) }
    }

    @Test
    fun `onConfirmAccountDeletion with RECENT_LOGIN_REQUIRED should trigger reauthentication`() = runTest {
        // Arrange
        coEvery { firebaseRepository.currentUser() } returns mockUser

        coEvery { firebaseRepository.deleteAccount() } returns Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_RECENT_LOGIN_REQUIRED)
        coEvery { firebaseRepository.getCurrentUserProviderId() } returns Result.Success(FirebaseProviderId.PASSWORD)

        // Act
        viewModel.onEvent(ProfileEvents.OnConfirmAccountDeletion(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { firebaseRepository.deleteAccount() }
        coVerify { firebaseRepository.getCurrentUserProviderId() }

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ProfileModalBottomSheetState.AUTHENTICATE_WITH_EMAIL, state.profileModalBottomSheetState)
        }
    }

    @Test
    fun `reauthenticateWithEmailAndPassword success should navigate to welcome screen`() = runTest {
        // Arrange
        viewModel.onEvent(ProfileEvents.OnEmailTextFieldValueChanged("test@example.com"))
        viewModel.onEvent(ProfileEvents.OnPasswordChanged("password123"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            firebaseRepository.reauthenticateWithEmailAndPassword("test@example.com", "password123")
        } returns Result.Success(Unit)

        // Act
        viewModel.onEvent(ProfileEvents.ReauthenticateWithEmailAndPassword(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify {
            firebaseRepository.reauthenticateWithEmailAndPassword("test@example.com", "password123")
        }
        verify { mockNavController.navigate(WelcomeScreen) }
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ProfileModalBottomSheetState.INACTIVE, state.profileModalBottomSheetState)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `reauthenticateWithEmailAndPassword error should update error state`() = runTest {
        // Arrange
        viewModel.onEvent(ProfileEvents.OnEmailTextFieldValueChanged("test@example.com"))
        viewModel.onEvent(ProfileEvents.OnPasswordChanged("password123"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            firebaseRepository.reauthenticateWithEmailAndPassword("test@example.com", "password123")
        } returns Result.Error(FirebaseReauthenticate.FIREBASE_AUTH_INVALID_CREDENTIALS)

        // Act
        viewModel.onEvent(ProfileEvents.ReauthenticateWithEmailAndPassword(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isReauthenticateWithEmailAndPasswordError)
            assertEquals("The supplied credentials do not correspond to the previously signed in user.", state.errorSupportingText)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `linkAccount success should update state`() = runTest {
        // Arrange
        viewModel.onEvent(ProfileEvents.OnEmailTextFieldValueChanged("test@example.com"))
        viewModel.onEvent(ProfileEvents.OnPasswordChanged("password123"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            firebaseRepository.linkAccountWithEmail("test@example.com", "password123")
        } returns Result.Success(Unit)

        // Act
        viewModel.onEvent(ProfileEvents.LinkAccount)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ProfileModalBottomSheetState.SUCCESS, state.profileModalBottomSheetState)
            assertFalse(state.isAnonymousUser)
        }
    }

    @Test
    fun `linkAccount error should update error state`() = runTest {
        // Arrange
        viewModel.onEvent(ProfileEvents.OnEmailTextFieldValueChanged("test@example.com"))
        viewModel.onEvent(ProfileEvents.OnPasswordChanged("password123"))
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            firebaseRepository.linkAccountWithEmail("test@example.com", "password123")
        } returns Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH_USER_COLLISION)

        // Act
        viewModel.onEvent(ProfileEvents.LinkAccount)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ProfileModalBottomSheetState.ERROR, state.profileModalBottomSheetState)
            assertEquals("The email address you entered is already associated with an account. Please try use a different email address.",
                state.profileModalBottomSheetErrorMessage)
        }
    }

    @Test
    fun `linkAccountWithGoogle success should update state`() = runTest {
        // Arrange
        coEvery { firebaseRepository.linkAccountWithGoogle() } returns Result.Success(Unit)

        // Act
        viewModel.onEvent(ProfileEvents.LinkAccountWithGoogle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isAnonymousUser)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `linkAccountWithGoogle error should update error state`() = runTest {
        // Arrange
        coEvery { firebaseRepository.linkAccountWithGoogle() } returns Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH_USER_COLLISION)

        // Act
        viewModel.onEvent(ProfileEvents.LinkAccountWithGoogle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            // We can't directly test the snackbar, but we could verify the state is correct
        }
    }

    @Test
    fun `openLinkAccountWithEmailModalBottomSheet should update state`() = runTest {
        // Act
        viewModel.onEvent(ProfileEvents.OpenLinkAccountWithEmailModalBottomSheet)

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ProfileModalBottomSheetState.LINK_ANONYMOUS_USER_WITH_EMAIL, state.profileModalBottomSheetState)
        }
    }

    @Test
    fun `reauthentication with google provider should call reauthenticateWithGoogle`() = runTest {
        // Arrange
        coEvery { firebaseRepository.deleteAccount() } returns Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_RECENT_LOGIN_REQUIRED)
        coEvery { firebaseRepository.getCurrentUserProviderId() } returns Result.Success(FirebaseProviderId.GOOGLE)
        coEvery { firebaseRepository.reauthenticateWithGoogle() } returns Result.Success(Unit)

        // Act
        viewModel.onEvent(ProfileEvents.OnConfirmAccountDeletion(mockNavController))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { firebaseRepository.deleteAccount() }
        coVerify { firebaseRepository.getCurrentUserProviderId() }
        coVerify { firebaseRepository.reauthenticateWithGoogle() }
        verify { mockNavController.navigate(WelcomeScreen) }
    }

}