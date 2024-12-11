package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

/**
 * Displays navigation buttons for moving between questions in a trivia game.
 *
 * Provides "Previous" and "Next" buttons to navigate through the questions.
 *
 * @param onEvent A lambda function that is triggered when a navigation button is clicked.
 * It receives a [TriviaMainEvents] object from ViewModel indicating the navigation action.
 */
@Composable
fun QuestionNavigationButtons(onEvent: (TriviaMainEvents) -> Unit) {
    /**
     * Todo: Finish Trivia Button
     * Todo: Hide Previous/Next button according to question index
     */
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = { onEvent(TriviaMainEvents.PreviousQuestion) },
            shape = RoundedCornerShape(10f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.NavigateBefore,
                contentDescription = "Navigate to previous question"
            )

            Text("Previous", fontFamily = poppinsFamily)
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = { onEvent(TriviaMainEvents.NextQuestion) },
            shape = RoundedCornerShape(10f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = false)
        ) {

            Text(text = "Next", fontFamily = poppinsFamily)

            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Navigate to next question"
            )
        }
    }
}