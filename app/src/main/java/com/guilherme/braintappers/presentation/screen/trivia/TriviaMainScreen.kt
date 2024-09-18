package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

@Composable
fun TriviaMainScreen(
    //Todo: Get category
    navController: NavHostController,
    numberOfQuestions: String,
    difficulty: String,
    type: String
) {

    val viewModel = koinViewModel<TriviaMainViewModel>()

    LaunchedEffect(Unit) {
        viewModel.fetchTrivia(
            numberOfQuestions = numberOfQuestions,
            categoryId = "",
            difficulty = difficulty,
            type = type
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()) {
        Text(text = numberOfQuestions)
        Text(text = difficulty)
        Text(text = type)
    }
}

