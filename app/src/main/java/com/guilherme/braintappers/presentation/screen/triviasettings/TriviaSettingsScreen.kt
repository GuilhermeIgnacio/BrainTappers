package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
                contentDescription = "Return to Previous Screen"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = state.numberOfQuestionsValue ?: "Number of Questions")
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEvent(TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu) }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
            }
        }

        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = state.isNumberOfQuestionsMenuOpen,
            onDismissRequest = { /*TODO*/ }
        ) {
            viewModel.numberOfQuestions.forEach {
                DropdownMenuItem(text = { Text(text =it.text) }, onClick = it.onClick)
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }

}