package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.component.TriviaSettingsDropdownMenu
import org.koin.androidx.compose.koinViewModel

@Composable
fun TriviaSettingsScreen(navController: NavController) {

    val viewModel = koinViewModel<TriviaSettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {

        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.content_description) // Return to Previous Screen
            )
        }

        TriviaSettingsDropdownMenu(
            text = if (!state.numberOfQuestionsValue.isNullOrEmpty())
                stringResource(id = R.string.question_value, state.numberOfQuestionsValue ?: "")
            else stringResource(id = R.string.number_of_questions),

            onClick = { onEvent(TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu) },
            isDropdownMenuOpen = state.isNumberOfQuestionsMenuOpen,
            dropdownItems = viewModel.numberOfQuestions,
            dismissDropdownMenu = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
        )

        TriviaSettingsDropdownMenu(
            text = state.difficultyValue?.asString() ?: stringResource(id = R.string.difficulty),
            onClick = { onEvent(TriviaSettingsEvents.OpenDifficultyMenu) },
            isDropdownMenuOpen = state.isDifficultyMenuOpen,
            dropdownItems = viewModel.difficulty,
            dismissDropdownMenu = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
        )

        TriviaSettingsDropdownMenu(
            text = state.typeValue?.asString() ?: "Select Type",
            onClick = { onEvent(TriviaSettingsEvents.OpenTypeMenu) },
            isDropdownMenuOpen = state.isTypeMenuOpen,
            dropdownItems = viewModel.type,
            dismissDropdownMenu = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
        )

    }

}