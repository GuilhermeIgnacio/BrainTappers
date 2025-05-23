package com.guilherme.braintappers.domain

import com.guilherme.braintappers.domain.model.ApiResponse

interface TriviaApiService {

    suspend fun fetchTriviaByCategory(
        numberOfQuestions: String,
        categoryId: String,
        difficulty: String,
        type: String
    ): com.guilherme.braintappers.domain.Result<ApiResponse, DataError>

}