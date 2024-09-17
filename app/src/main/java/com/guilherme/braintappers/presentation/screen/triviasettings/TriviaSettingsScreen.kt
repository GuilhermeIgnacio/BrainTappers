package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.component.TriviaSettingsDropdownMenu
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
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

        Spacer(modifier = Modifier.height(32.dp))

        TriviaSettingsDropdownMenu(
            text = state.difficultyValue?.asString() ?: stringResource(id = R.string.difficulty),
            onClick = { onEvent(TriviaSettingsEvents.OpenDifficultyMenu) },
            isDropdownMenuOpen = state.isDifficultyMenuOpen,
            dropdownItems = viewModel.difficulty,
            dismissDropdownMenu = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        TriviaSettingsDropdownMenu(
            text = state.typeValue?.asString() ?: stringResource(id = R.string.select_type),
            onClick = { onEvent(TriviaSettingsEvents.OpenTypeMenu) },
            isDropdownMenuOpen = state.isTypeMenuOpen,
            dropdownItems = viewModel.type,
            dismissDropdownMenu = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            onClick = { },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(id = R.string.start),
                fontFamily = poppinsFamily
            )
        }

    }

}