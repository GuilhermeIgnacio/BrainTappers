package com.guilherme.braintappers.presentation.screen.quizplayeddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.navigation.NavController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.QuizzesPlayedScreen
import com.guilherme.braintappers.presentation.screen.trivia.parseHtml
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun QuizPlayedDetailScreen(
    navController: NavController,
    questions: List<String>,
    userAnswers: List<String>,
    correctAnswers: List<String>,
) {

    val foo = questions.zip(userAnswers).zip(correctAnswers)

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            IconButton(
                onClick = { navController.navigate(QuizzesPlayedScreen) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Return to Quizzes Played Screen"
                )
            }
        }

        items(foo) {

            val question = it.first.first
            val userAnswer = it.first.second
            val correctAnswer = it.second

            val isCorrect = userAnswer == correctAnswer

            ElevatedCard(
                modifier = Modifier.padding(12.dp),
                onClick = {}
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .background(if (isCorrect) primaryColor else Color(0xFFE40001))
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = question.parseHtml(),
                        fontFamily = poppinsFamily,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Your Answer",
                        fontFamily = poppinsFamily,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = if (isCorrect) painterResource(R.drawable.circle_check_solid) else painterResource(R.drawable.circle_xmark_solid_red),
                            tint = if (isCorrect) primaryColor else Color.Red,
                            contentDescription = ""
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = userAnswer,
                            fontFamily = poppinsFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isCorrect) {

                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "Correct Answer",
                            fontFamily = poppinsFamily,
                            style = MaterialTheme.typography.bodySmall
                        )


                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(R.drawable.circle_regular),
                                contentDescription = "",
                                tint = primaryColor
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = correctAnswer,
                                fontFamily = poppinsFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                }
            }
        }

    }

}