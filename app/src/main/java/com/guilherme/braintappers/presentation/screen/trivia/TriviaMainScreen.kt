package com.guilherme.braintappers.presentation.screen.trivia

import android.annotation.SuppressLint
import android.text.Html
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.DisplayResult
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TriviaMainScreen(
    navController: NavHostController,
    categoryId: String,
    numberOfQuestions: String,
    difficulty: String,
    type: String
) {

    val viewModel = koinViewModel<TriviaMainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    var isDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchTrivia(
            numberOfQuestions = numberOfQuestions,
            categoryId = "", //Todo: Get Category
            difficulty = difficulty,
            type = type
        )

    }

    state.result?.DisplayResult(
        onSuccess = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
            ) {
                // Close Trivia Button
                IconButton(onClick = {
                    isDialogOpen = !isDialogOpen
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }

                val questions = state.questions
                val questionIndex = state.questionIndex
                val answers = state.answers

                Spacer(modifier = Modifier.height(8.dp))

                /**
                 * Displays a row of numbered buttons for navigating between questions
                 */
                NumberedNavigationButtons(
                    questions = questions,
                    state = state,
                    questionIndex = questionIndex,
                    onEvent = onEvent
                )

                Spacer(modifier = Modifier.height(8.dp))

                /**
                 * Displays a trivia question and its answer options, with animated transitions between questions.
                 */
                QuestionDisplay(
                    questionIndex = questionIndex,
                    questions = questions,
                    answers = answers,
                    state = state,
                    onEvent = onEvent
                )

                Spacer(modifier = Modifier.height(8.dp))

                /**
                 * Displays a "Finish" button that becomes visible when the user reaches the last question of the trivia.
                 */
                FinishTriviaButton(questions = questions, questionIndex = questionIndex, isFinished = state.isTriviaFinished, onEvent = onEvent)

            }

            /**
             * Confirmation dialog displayed when the user attempts to exit the trivia screen.
             */
            if (isDialogOpen) {
                CloseTriviaDialog(navController, closeDialog = { isDialogOpen = false })
            }

            CustomCircularProgressIndicator(visible = state.isLoading)

        },
        onError = {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(250.dp),
                    painter = painterResource(id = R.drawable.error_icon),
                    contentDescription = "Error Icon"
                )

                when (it) {
                    DataError.UNKNOWN -> {

                    }
                }

            }

        }
    )

}

fun String.parseHtml(): String {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
}

data class UserTrivia(
    val question: String,
    val correctAnswer: String,
    val selectedAnswer: String
)

