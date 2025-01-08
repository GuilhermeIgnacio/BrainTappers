package com.guilherme.braintappers.domain

import com.guilherme.braintappers.domain.model.QuizResults

interface FirebaseFirestoreRepository {
    suspend fun write(quizUid: String, data: HashMap<String, Any?>): Result<Unit, FirestoreError>
    suspend fun getUserPlayedQuizzes(): Result<List<QuizResults>, FirebaseGetUserQuizzes>
    suspend fun deleteData(): Result<Unit, FirebaseFirestoreDeleteError>
}