package com.guilherme.braintappers.presentation.screen.trivia

import android.text.Html
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.DisplayResult
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

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

                Text(
                    text = questions[questionIndex].question.parseHtml(),
                    fontWeight = FontWeight.Bold
                )

                LazyColumn {
                    items(answers[questionIndex]) {
                        Text(text = it.parseHtml())
                    }
                }

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

