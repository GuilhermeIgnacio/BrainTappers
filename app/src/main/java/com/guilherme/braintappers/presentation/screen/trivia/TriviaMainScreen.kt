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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.DisplayResult
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import com.guilherme.braintappers.presentation.screen.trivia.components.CloseTriviaDialog
import com.guilherme.braintappers.presentation.screen.trivia.components.FinishTriviaButton
import com.guilherme.braintappers.presentation.screen.trivia.components.NumberedNavigationButtons
import com.guilherme.braintappers.presentation.screen.trivia.components.QuestionDisplay
import com.guilherme.braintappers.util.poppinsFamily
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
            categoryId = categoryId,
            difficulty = difficulty,
            type = type
        )

    }

    state.result?.DisplayResult(
        onSuccess = {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) }
            ) { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                ) {
                    // Close Trivia Button
                    IconButton(onClick = {

                        if (!state.isTriviaFinished) {
                            isDialogOpen = !isDialogOpen
                        } else {
                            navController.navigate(HomeScreen)
                        }

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
                    FinishTriviaButton(
                        questions = questions,
                        questionIndex = questionIndex,
                        isFinished = state.isTriviaFinished,
                        onEvent = onEvent
                    )

                }
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

            var errorMessage by remember { mutableStateOf("") }

            when (it) {
                DataError.NOT_FOUND -> {
                    errorMessage = "Content Not Found"
                }

                DataError.SERVICE_UNAVAILABLE -> {
                    errorMessage = "Service Unavailable"
                }

                DataError.BAD_GATEWAY -> {
                    errorMessage = "Bad Gateway"
                }

                DataError.FORBIDDEN -> {
                    errorMessage = "Forbidden"
                }

                DataError.UNAUTHORIZED -> {
                    errorMessage = "Unauthorized"
                }

                DataError.NO_INTERNET -> {
                    errorMessage =
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
                }

                DataError.UNKNOWN -> {
                    errorMessage = "Unknown error, please restart the app or try later."
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(300.dp),
                    painter = painterResource(id = R.drawable.error_icon),
                    contentDescription = "Error Icon"
                )


                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    text = errorMessage,
                    fontFamily = poppinsFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { navController.navigate(HomeScreen) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Return to Home",
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline
                    )
                }


            }

        }
    )

    CustomCircularProgressIndicator(state.isLoading)

}

fun String.parseHtml(): String {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
}