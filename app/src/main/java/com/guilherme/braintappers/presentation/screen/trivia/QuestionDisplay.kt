package com.guilherme.braintappers.presentation.screen.trivia

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

/**
 * Displays a trivia question and its answer options, with animated transitions between questions.
 *
 * @param questionIndex The index of the current question to display.
 * @param questions The list of all trivia questions.
 * @param answers A list of answer options for each question.
 * @param state The current state of the trivia game, including user answers.
 * @param onEvent A lambda function to handle events, such as answer selection.
 */

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun QuestionDisplay(
    questionIndex: Int,
    questions: List<Question>,
    answers: List<List<String>>,
    state: TriviaMainState,
    onEvent: (TriviaMainEvents) -> Unit
) {
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

                val isCorrect =
                    state.userAnswers[questionIndex] == state.questions[questionIndex].correctAnswer

                val isFinished = state.isTriviaFinished


                val notFinishedTriviaColors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSelected) Color.White else Color.Black,
                    containerColor = if (isSelected) primaryColor else Color.Transparent
                )

                val finishedTriviaColors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isCorrect && isSelected) Color.White else if (!isCorrect && isSelected) Color.White else Color.Black,
                    containerColor = if (isCorrect && isSelected) primaryColor else if (!isCorrect && isSelected) Color.Red else Color.Transparent
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10f),
                    colors = if (isFinished) finishedTriviaColors else notFinishedTriviaColors,
                    border = ButtonDefaults.outlinedButtonBorder(enabled = !isSelected),
                    onClick = {
                        if (!state.isTriviaFinished) {
                            onEvent(TriviaMainEvents.OnAnswerClicked(it.parseHtml()))
                        }
                    }) {
                    Text(
                        text = it.parseHtml(),
                        fontFamily = poppinsFamily
                    )
                }
            }


            // Display answer feedback only when the trivia is finished
            item {
                AnimatedVisibility(
                    visible = state.isTriviaFinished,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    //Check if user answer is correct
                    val foo =
                        state.userAnswers[questionIndex] == state.questions[questionIndex].correctAnswer

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (foo) primaryColor else Color.Red
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            Spacer(modifier = Modifier.width(8.dp))

                            Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterVertically),
                                painter = painterResource(
                                    if (foo) R.drawable.circle_check_regular else R.drawable.circle_xmark_regular
                                ),
                                contentDescription = if (foo) "" else ""
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = if (foo) "Correct Answer!" else "Incorrect Answer. Correct Answer: ${state.questions[questionIndex].correctAnswer}",
                                fontFamily = poppinsFamily,
                                color = Color.White,
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            )
                        }
                    }
                }

            }

        }
    }
}