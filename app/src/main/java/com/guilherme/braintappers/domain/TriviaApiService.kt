package com.guilherme.braintappers.domain

interface TriviaApiService {

    suspend fun fetchTriviaByCategory(categoryId: String)

}