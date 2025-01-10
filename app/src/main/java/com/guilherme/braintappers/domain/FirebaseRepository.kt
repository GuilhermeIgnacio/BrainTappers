package com.guilherme.braintappers.domain

import com.google.firebase.auth.FirebaseUser
import com.guilherme.braintappers.data.FirebaseProviderId

interface FirebaseRepository {

    suspend fun currentUser(): FirebaseUser?
    suspend fun createAnonymousAccount()
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit, FirebaseEmailAndPasswordAuthError>
    suspend fun signUpWithGoogle(idToken: String): Result<Unit, FirebaseGoogleAuthError>
    suspend fun signInWithEmail(email: String, password: String): Result<Unit, FirebaseSignInWithEmailAndPasswordError>
    suspend fun linkAccountWithEmail(email: String, password: String): Result<Unit, LinkAccountWithEmailError>
    suspend fun linkAccountWithGoogle(): Result<Unit, LinkAccountWithGoogleError>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit, FirebaseAccountDeletion>
    suspend fun getCurrentUserProviderId(): Result<FirebaseProviderId, FirebaseCurrentUser>
    suspend fun reauthenticateWithEmailAndPassword(email: String, password: String): Result<Unit, FirebaseReauthenticate>
    suspend fun reauthenticateWithGoogle(): Result<Unit, FirebaseReauthenticate>
    suspend fun resetPassword(email: String): Result<Unit, ResetPasswordError>
}