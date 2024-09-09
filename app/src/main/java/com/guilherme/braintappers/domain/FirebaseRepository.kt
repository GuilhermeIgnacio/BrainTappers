package com.guilherme.braintappers.domain

interface FirebaseRepository {
    suspend fun signUpWithGoogle(idToken: String)
}