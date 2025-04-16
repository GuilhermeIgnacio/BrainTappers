package com.guilherme.braintappers.presentation.screen.triviasettings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.DropdownItem
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

/**
 * This code defines a screen for trivia settings using Jetpack Compose. It allows the user to
 * customize the number of questions, difficulty, and type of questions for a trivia game.
 *
 * This Screen Appears Before TriviaMainScreen
 */
@Composable
fun TriviaSettingsScreen(
    navController: NavController,
    categoryId: String,
) {

    val viewModel = koinViewModel<TriviaSettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    LaunchedEffect(Unit) {
        viewModel.setCategoryId(categoryId = categoryId)
        onEvent(TriviaSettingsEvents.OnDifficultySelected(viewModel.difficulty[0]))
        onEvent(TriviaSettingsEvents.OnTypeSelected(viewModel.type[1]))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        IconButton(onClick = { navController.navigate(HomeScreen) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.content_description) // Return to Previous Screen
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = "Number of Questions",
            fontFamily = poppinsFamily
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .border(2.dp, color = Color.LightGray)
                .clickable { onEvent(TriviaSettingsEvents.OpenNumberOfQuestionsDropdownMenu) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.numberOfQuestionsValue?.text?.asString() ?: "10 Questions",
                    fontFamily = poppinsFamily
                )

                val icon by animateFloatAsState(targetValue = if (state.isNumberOfQuestionsMenuOpen) 180f else 0f)

                Icon(
                    modifier = Modifier.graphicsLayer(rotationZ = icon),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = ""
                )

            }
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = state.isNumberOfQuestionsMenuOpen,
                onDismissRequest = { onEvent(TriviaSettingsEvents.DismissDropdownMenu) }
            ) {
                viewModel.numberOfQuestions.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it.text.asString(),
                                fontFamily = poppinsFamily
                            )
                        },
                        onClick = { it.onClick(it) }
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(32.dp))

        /**
         * Difficulty level button row
         */
        SelectableButtonRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            header = "Difficulty",
            selectedItemValue = state.difficultyValue,
            items = viewModel.difficulty
        )

        /**
         * Mixed Difficulties button
         */
        val isSelected = state.difficultyValue == viewModel.difficulty[0]

        val selectedColors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isSelected) Color.White else Color.DarkGray,
            containerColor = if (isSelected) primaryColor else Color.Transparent
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(5f),
            onClick = { onEvent(TriviaSettingsEvents.OnDifficultySelected(viewModel.difficulty[0])) },
            border = BorderStroke(
                ButtonDefaults.outlinedButtonBorder().width,
                color = if (isSelected) primaryColor else Color.Gray
            ),
            colors = selectedColors
        ) {
            Text(text = "Mixed", fontFamily = poppinsFamily)
        }

        Spacer(modifier = Modifier.height(32.dp))

        /**
         * Question Types button row
         */
        SelectableButtonRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            header = "Question Types",
            selectedItemValue = state.typeValue,
            items = viewModel.type
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            onClick = { onEvent(TriviaSettingsEvents.OnStartButtonClicked(navController)) },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(id = R.string.start),
                color = Color.White,
                fontFamily = poppinsFamily
            )
        }

    }

}

@Composable
private fun SelectableButtonRow(
    modifier: Modifier = Modifier,
    header: String,
    selectedItemValue: DropdownItem?,
    items: List<DropdownItem>,
) {
    Text(
        modifier = modifier,
        text = header,
        fontFamily = poppinsFamily
    )
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {


        items.forEach {
            val isSelected = selectedItemValue == it

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.DarkGray,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) primaryColor else Color.Transparent,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )

            val selectedColors = ButtonDefaults.outlinedButtonColors(
                contentColor = contentColor,
                containerColor = containerColor
            )

            if (it.text.asString().isNotBlank()) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(7f),
                    onClick = { it.onClick(it) },
                    border = BorderStroke(
                        ButtonDefaults.outlinedButtonBorder().width,
                        color = if (isSelected) primaryColor else Color.Gray
                    ),
                    colors = selectedColors,
                    contentPadding = PaddingValues(0.dp)
                ) {

                    Text(text = it.text.asString(), fontFamily = poppinsFamily)

                }
            }
        }
    }
}