package com.guilherme.braintappers.presentation.screen.quizzesplayed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuizzesPlayedScreen(navController: NavController) {

    val viewModel = koinViewModel<QuizzesPlayedViewModel>()

    Column(
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
    ) {
        Text("Lorem")
    }
}