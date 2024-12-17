package com.guilherme.braintappers.presentation.screen.trivia

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
}