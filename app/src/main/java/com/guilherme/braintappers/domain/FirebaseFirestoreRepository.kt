package com.guilherme.braintappers.domain

interface FirebaseFirestoreRepository {
    suspend fun write(quizUid: String, data: HashMap<String, List<String>>): Result<Unit, FirestoreError>
}