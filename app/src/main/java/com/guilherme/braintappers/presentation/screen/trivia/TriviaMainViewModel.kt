package com.guilherme.braintappers.presentation.screen.trivia

import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.domain.TriviaApiService

class TriviaMainViewModel(
    private val trivia: TriviaApiService
) : ViewModel() {

    suspend fun fetchTrivia(
        numberOfQuestions: String,
        categoryId: String,
        difficulty: String,
        type: String
    ) {
        println(
            trivia.fetchTriviaByCategory(
                numberOfQuestions = numberOfQuestions,
                categoryId = categoryId,
                difficulty = difficulty,
                type = type,
            )
        )
    }

}