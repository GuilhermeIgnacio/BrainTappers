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
    navController: NavHostController,
    categoryId: String,
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

