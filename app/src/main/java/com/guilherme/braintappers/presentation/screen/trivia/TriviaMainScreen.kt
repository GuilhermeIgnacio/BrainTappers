package com.guilherme.braintappers.presentation.screen.trivia

import android.annotation.SuppressLint
import android.text.Html
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.DisplayResult
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalLayoutApi::class)
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

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                ) {
                    items(questions.size) {

                        // Check if the question was answered by the user
                        val isAnswered = state.userAnswers[it].isNotBlank()

                        val current by animateColorAsState(
                            targetValue = if (questions[questionIndex].question == questions[it].question) Color.Gray else Color.Transparent,
                            label = ""
                        )

                        OutlinedButton(
                            modifier = Modifier.size(40.dp),
                            onClick = {
                                onEvent(TriviaMainEvents.NavigateToQuestion(it))
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isAnswered) primaryColor else Color.Transparent,
                                contentColor = if (isAnswered) Color.White else Color.Black
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = current
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = (it + 1).toString(),
                                fontFamily = poppinsFamily,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = questionIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(300)
                            )
                        } else {
                            slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(300)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(300)
                            )
                        }
                    },
                    label = ""
                ) {

                    LazyColumn {

                        item {
                            // Question
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = questions[questionIndex].question.parseHtml(),
                                textAlign = TextAlign.Center,
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        }

                        items(answers[questionIndex]) {

                            // Check if the current answer is selected by the user
                            val isSelected = state.userAnswers[questionIndex] == it.parseHtml()



                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isSelected) Color.White else Color.Black,
                                    containerColor = if (isSelected) primaryColor else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = !isSelected),
                                onClick = {
                                    onEvent(TriviaMainEvents.OnAnswerClicked(it.parseHtml()))
                                }) {
                                Text(
                                    text = it.parseHtml(),
                                    fontFamily = poppinsFamily
                                )
                            }
                        }

                    }
                }

                /**
                 * Navigation buttons for going to the next, previous question or finish when in the last question
                 */
                QuestionNavigationButtons(
                    onEvent = onEvent,
                    questionIndex = questionIndex,
                    questionsListSize = questions.size,
                )

            }

            /**
             * Confirmation dialog displayed when the user attempts to exit the trivia screen.
             */
            if (isDialogOpen) {
                CloseTriviaDialog(navController, closeDialog = { isDialogOpen = false })
            }

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
                    DataError.Response.UNKNOWN -> {

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

