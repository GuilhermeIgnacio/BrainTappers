package com.guilherme.braintappers.presentation.screen.quizzesplayed

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.guilherme.braintappers.navigation.ProfileScreen
import com.guilherme.braintappers.navigation.QuizPlayedDetailScreen
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizzesPlayedScreen(navController: NavController) {

    val viewModel = koinViewModel<QuizzesPlayedViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val quizResults = state.quizResults

    if (quizResults.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),

            ) {

            item {
                IconButton(
                    onClick = { navController.navigate(ProfileScreen) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return to Quizzes Played Screen"
                    )
                }
            }

            items(state.quizResults) {

                val instant = Instant.ofEpochSecond(it.createdAt?.seconds ?: 0)

                val formatter =
                    DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

                val formattedDate = formatter.format(instant)

                val correctAnswers =
                    it.userAnswers.zip(it.correctAnswers).filter { (a, b) -> a == b }
                        .map { (a, _) -> a }

                Surface(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 16.dp,
                    onClick = {
                        navController.navigate(
                            QuizPlayedDetailScreen(
                                questions = it.questions,
                                userAnswers = it.userAnswers,
                                correctAnswers = it.correctAnswers
                            )
                        )
                    },
                    color = primaryColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {

                            Text(
                                text = "Quiz Played on $formattedDate",
                                fontFamily = poppinsFamily,
                                fontStyle = FontStyle.Italic
                            )

                            Text(
                                text = it.category ?: "",
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.SemiBold
                            )

                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column(

                        ) {
                            Text(
                                text = "Answers",
                                fontFamily = poppinsFamily,
                            )

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = correctAnswers.size.toString() + "/" + it.questions.size,
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {

            IconButton(
                modifier = Modifier.align(Alignment.TopStart),
                onClick = { navController.navigate(ProfileScreen) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Quizzes Played Screen"
                )
            }

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "No quizzes played yet! Start exploring and put your knowledge to the test!",
                fontFamily = poppinsFamily,
                textAlign = TextAlign.Center
            )
        }
    }

}