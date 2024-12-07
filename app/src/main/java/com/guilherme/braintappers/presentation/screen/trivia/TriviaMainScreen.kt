package com.guilherme.braintappers.presentation.screen.trivia

import android.text.Html
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.DisplayResult
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

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
                    .statusBarsPadding()
            ) {
                val questions = it.results

                val answers = remember(state.currentQuestion) {
                    val answers =
                        questions[state.currentQuestion].incorrectAnswers.toMutableList()
                    answers.add(questions[state.currentQuestion].correctAnswer)
                    answers.shuffled().map { it.parseHtml() }
                }

                val linearProgressIndicator by animateFloatAsState(
                    targetValue = state.currentQuestion.plus(1) / questions.size.toFloat(),
                )

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = primaryColor,
                    progress = { linearProgressIndicator }
                )


                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }

                AnimatedContent(targetState = state.currentQuestion) { currentQuestion ->

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = questions[currentQuestion].question.parseHtml(),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = poppinsFamily
                        )

                        answers.forEach {

                            val animatedButtonColor by animateColorAsState(
                                targetValue = if (state.selectedAnswer[state.currentQuestion] == it) primaryColor else Color.Transparent,
                                animationSpec = tween(200, 0, LinearEasing), label = ""
                            )


                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (state.selectedAnswer[state.currentQuestion] == it) Color.White else Color.Black,
                                    containerColor = animatedButtonColor,
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(
                                    enabled = state.selectedAnswer[state.currentQuestion] != it
                                ),
                                onClick = {
                                    val userTrivia = UserTrivia(
                                        question = questions[state.currentQuestion].question.parseHtml(),
                                        correctAnswer = questions[state.currentQuestion].correctAnswer.parseHtml(),
                                        selectedAnswer = it
                                    )

                                    onEvent(TriviaMainEvents.OnAnswerClicked(userTrivia))

                                }
                            ) {
                                Text(
                                    text = it,
                                    fontFamily = poppinsFamily,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            }
                        }

                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onEvent(TriviaMainEvents.PreviousQuestion) },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Previous Question", fontFamily = poppinsFamily)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (state.currentQuestion != questions.size - 1)
                                onEvent(TriviaMainEvents.NextQuestion)
                            else onEvent(TriviaMainEvents.FinishTrivia)
                        },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                        )
                    ) {

                        AnimatedContent(
                            targetState = state.currentQuestion == questions.size - 1
                        ) {
                            Text(
                                text = if (!it) "Next Question" else "Finish",
                                fontFamily = poppinsFamily
                            )
                        }

                    }

                }


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

                when(it){
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

