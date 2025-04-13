package com.guilherme.braintappers

import androidx.navigation.NavController
import app.cash.turbine.test
import com.guilherme.braintappers.domain.model.DropdownItem
import com.guilherme.braintappers.navigation.TriviaScreen
import com.guilherme.braintappers.presentation.UiText
import com.guilherme.braintappers.presentation.screen.triviasettings.TriviaSettingsEvents
import com.guilherme.braintappers.presentation.screen.triviasettings.TriviaSettingsViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
class TriviaSettingsViewModelTest : KoinTest {

    private val viewModel by inject<TriviaSettingsViewModel>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                module {
                    factory { TriviaSettingsViewModel() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `initial state is set correctly`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertNull(initialState.categoryId)
            assertFalse(initialState.isNumberOfQuestionsMenuOpen)
            assertNull(initialState.numberOfQuestionsValue)
            assertFalse(initialState.isDifficultyMenuOpen)
            assertNull(initialState.difficultyValue)
            assertFalse(initialState.isTypeMenuOpen)
            assertNull(initialState.typeValue)
        }
    }

    @Test
    fun `setCategoryId updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Set category ID
            viewModel.setCategoryId("9")

            // Verify updated state
            val updatedState = awaitItem()
            assertEquals("9", updatedState.categoryId)
        }
    }

    @Test
    fun `OpenNumberOfQuestionsDropdownMenu event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Open dropdown menu
            viewModel.onEvent(TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu)

            // Verify updated state
            val updatedState = awaitItem()
            assertTrue(updatedState.isNumberOfQuestionsMenuOpen)
        }
    }

    @Test
    fun `OnNumberOfQuestionsSelected event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Create a test dropdown item
            val testItem = DropdownItem(
                apiParameter = "amount=5",
                text = UiText.StringResource(R.string.questions, "5"),
                onClick = {}
            )

            // Select number of questions
            viewModel.onEvent(TriviaSettingsEvents.OnNumberOfQuestionsSelected(testItem))

            // Verify updated state
            val updatedState = awaitItem()
            assertEquals(testItem, updatedState.numberOfQuestionsValue)
            assertFalse(updatedState.isNumberOfQuestionsMenuOpen)
        }
    }

    @Test
    fun `OpenDifficultyMenu event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Open difficulty menu
            viewModel.onEvent(TriviaSettingsEvents.OpenDifficultyMenu)

            // Verify updated state
            val updatedState = awaitItem()
            assertTrue(updatedState.isDifficultyMenuOpen)
        }
    }

    @Test
    fun `OnDifficultySelected event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Create a test dropdown item
            val testItem = DropdownItem(
                apiParameter = "&difficulty=medium",
                text = UiText.StringResource(R.string.medium),
                onClick = {}
            )

            // Select difficulty
            viewModel.onEvent(TriviaSettingsEvents.OnDifficultySelected(testItem))

            // Verify updated state
            val updatedState = awaitItem()
            assertEquals(testItem, updatedState.difficultyValue)
            assertFalse(updatedState.isDifficultyMenuOpen)
        }
    }

    @Test
    fun `OpenTypeMenu event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Open type menu
            viewModel.onEvent(TriviaSettingsEvents.OpenTypeMenu)

            // Verify updated state
            val updatedState = awaitItem()
            assertTrue(updatedState.isTypeMenuOpen)
        }
    }

    @Test
    fun `OnTypeSelected event updates state correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Create a test dropdown item
            val testItem = DropdownItem(
                apiParameter = "&type=multiple",
                text = UiText.StringResource(R.string.multiple_choice),
                onClick = {}
            )

            // Select type
            viewModel.onEvent(TriviaSettingsEvents.OnTypeSelected(testItem))

            // Verify updated state
            val updatedState = awaitItem()
            assertEquals(testItem, updatedState.typeValue)
            assertFalse(updatedState.isTypeMenuOpen)
        }
    }

    @Test
    fun `DismissDropdownMenu event closes all menus`() = runTest {
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // First open all menus
            viewModel.onEvent(TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu)
            viewModel.onEvent(TriviaSettingsEvents.OpenDifficultyMenu)
            viewModel.onEvent(TriviaSettingsEvents.OpenTypeMenu)

            // Skip intermediate states
            skipItems(3)

            // Dismiss all menus
            viewModel.onEvent(TriviaSettingsEvents.DismissDropdownMenu)

            // Verify updated state
            val updatedState = awaitItem()
            assertFalse(updatedState.isNumberOfQuestionsMenuOpen)
            assertFalse(updatedState.isDifficultyMenuOpen)
            assertFalse(updatedState.isTypeMenuOpen)
        }
    }

    @Test
    fun `OnStartButtonClicked uses default values when selections are null`() = runTest {
        // Setup NavController mock
        val navController = mockk<NavController>(relaxed = true)
        val routeSlot = slot<TriviaScreen>()

        // Configure mocks
        every { navController.navigate(capture(routeSlot)) } returns Unit

        // Don't set any values, use defaults
        viewModel.state.test {
            // Skip initial state
            awaitItem()

            // Trigger navigation with default values
            viewModel.onEvent(TriviaSettingsEvents.OnStartButtonClicked(navController))

            // Verify navigation was called
            verify { navController.navigate(any<TriviaScreen>()) }

            // Verify default parameters were passed
            val capturedRoute = routeSlot.captured
            assertEquals("", capturedRoute.categoryId)
            assertEquals("amount=10", capturedRoute.numberOfQuestions)
            assertEquals("", capturedRoute.difficulty)
            assertEquals("", capturedRoute.type)
        }
    }

    @Test
    fun `numberOfQuestions list is correctly initialized`() {
        assertEquals(10, viewModel.numberOfQuestions.size)

        for (i in 0 until 10) {
            val item = viewModel.numberOfQuestions[i]
            assertEquals("amount=${i+1}", item.apiParameter)
            assertNotNull(item.onClick)
        }
    }

    @Test
    fun `difficulty list is correctly initialized`() {
        assertEquals(4, viewModel.difficulty.size)

        // Any difficulty (first item)
        assertEquals("", viewModel.difficulty[0].apiParameter)
        // Easy difficulty
        assertEquals("&difficulty=easy", viewModel.difficulty[1].apiParameter)
        // Medium difficulty
        assertEquals("&difficulty=medium", viewModel.difficulty[2].apiParameter)
        // Hard difficulty
        assertEquals("&difficulty=hard", viewModel.difficulty[3].apiParameter)
    }

    @Test
    fun `type list is correctly initialized`() {
        assertEquals(3, viewModel.type.size)

        // Multiple choice
        assertEquals("&type=multiple", viewModel.type[0].apiParameter)
        // Any type
        assertEquals("", viewModel.type[1].apiParameter)
        // True/False
        assertEquals("&type=boolean", viewModel.type[2].apiParameter)
    }
}