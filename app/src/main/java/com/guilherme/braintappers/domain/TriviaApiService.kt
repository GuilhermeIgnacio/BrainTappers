package com.guilherme.braintappers.domain

import com.guilherme.braintappers.domain.model.ApiResponse
import kotlin.Result

interface TriviaApiService {

    suspend fun fetchTriviaByCategory(categoryId: String): com.guilherme.braintappers.domain.Result<ApiResponse, DataError>

}