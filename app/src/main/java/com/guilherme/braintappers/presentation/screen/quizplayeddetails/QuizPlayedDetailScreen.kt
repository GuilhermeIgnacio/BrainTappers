package com.guilherme.braintappers.presentation.screen.quizplayeddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guilherme.braintappers.navigation.QuizzesPlayedScreen
import com.guilherme.braintappers.presentation.screen.trivia.parseHtml
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun QuizPlayedDetailScreen(
    navController: NavController,
    questions: List<String>,
    userAnswers: List<String>,
    correctAnswers: List<String>
) {

    val foo = questions.zip(userAnswers).zip(correctAnswers)

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {

        item {
            IconButton(
                onClick = { navController.navigate(QuizzesPlayedScreen) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Quizzes Played Screen"
                )
            }
        }

        items(foo) {

            val question = it.first.first
            val userAnswer = it.first.second
            val correctAnswer = it.second

            val isCorrect = userAnswer == correctAnswer

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (isCorrect) primaryColor else Color(0xFFE40001),
                contentColor = if (isCorrect) Color.Unspecified else Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = question.parseHtml(),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your Answer",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = userAnswer,
                        fontFamily = poppinsFamily
                    )

                    if (!isCorrect) {
                        Text(
                            text = "Correct Answer",
                            fontFamily = poppinsFamily,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = correctAnswer,
                            fontFamily = poppinsFamily
                        )
                    }

                }
            }

        }
    }

}