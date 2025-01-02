package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.guilherme.braintappers.domain.model.Question
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

/**
 * Displays a "Finish" button that becomes visible when the user reaches the last question of the trivia.
 *
 * This button is used to indicate the completion of the trivia game.
 *
 * @param questions The list of all trivia questions.
 * @param questionIndex The index of the currently displayed question.
 */

@Composable
fun FinishTriviaButton(
    questions: List<Question>,
    questionIndex: Int,
    onEvent: (TriviaMainEvents) -> Unit
) {
    AnimatedVisibility(
        visible = questions.size == questionIndex + 1,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onEvent(TriviaMainEvents.OnFinishButtonClicked) },
            shape = RoundedCornerShape(10f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = false)
        ) {
            Text(
                text = "Finish",
                fontFamily = poppinsFamily
            )
        }
    }
}