package com.guilherme.braintappers.domain

import com.google.firebase.auth.FirebaseUser

interface FirebaseRepository {

    suspend fun currentUser(): FirebaseUser?
    suspend fun createAnonymousAccount()
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit, FirebaseEmailAndPasswordAuthError>
    suspend fun signUpWithGoogle(idToken: String): Result<Unit, FirebaseGoogleAuthError>
    suspend fun signInWithEmail(email: String, password: String): Result<Unit, FirebaseSignInWithEmailAndPasswordError>

}