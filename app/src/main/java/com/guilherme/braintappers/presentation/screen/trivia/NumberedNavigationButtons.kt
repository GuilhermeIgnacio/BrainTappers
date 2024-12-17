package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

/**
 * Displays a row of numbered buttons for navigating between questions.
 *
 * Each button represents a question, and its appearance indicates whether the question has been answered.
 * The currently active question is highlighted.
 *
 * @param questions The list of all trivia questions.
 * @param state The current state of the trivia game, including user answers.
 * @param questionIndex The index of the currently displayed question.
 * @param onEvent A lambda function to handle events, such as navigating to a specific question.
 */

@Composable
fun NumberedNavigationButtons(
    questions: List<Question>,
    state: TriviaMainState,
    questionIndex: Int,
    onEvent: (TriviaMainEvents) -> Unit
) {
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
}