package com.guilherme.braintappers.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseEmailAndPasswordAuthError
import com.guilherme.braintappers.domain.FirebaseGoogleAuthError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.FirebaseSignInWithEmailAndPasswordError
import com.guilherme.braintappers.domain.Result
import kotlinx.coroutines.tasks.await

class FirebaseImpl : FirebaseRepository {

    override suspend fun currentUser(): FirebaseUser? {
        return try {
            Firebase.auth.currentUser
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun createAnonymousAccount() {
        if (currentUser() == null) {
            Firebase.auth.signInAnonymously().await()
            println("Anonymous user created")
        } else {
            println("Anonymous user already created")
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<Unit, FirebaseEmailAndPasswordAuthError> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_AUTH_USER_COLLISION)
        } catch (e: FirebaseNetworkException) {
            Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_NETWORK)
        } catch (e: Exception) {
            println(e)
            Result.Error(FirebaseEmailAndPasswordAuthError.UNKNOWN)
        }

    }

    override suspend fun signUpWithGoogle(idToken: String): Result<Unit, FirebaseGoogleAuthError> {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        return try {

            Firebase.auth.signInWithCredential(firebaseCredential).await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidUserException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseAuthUserCollisionException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_USER_COLLISION)

        } catch (e: Exception) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.UNKNOWN)

        }

    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit, FirebaseSignInWithEmailAndPasswordError> {
        return try {

            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.UNKNOWN)

        }

    }

    override suspend fun signOut() {
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Sign Out: $e")
        }
    }

    override suspend fun deleteAccount(): Result<Unit, FirebaseAccountDeletion> {
        return try {
            Firebase.auth.currentUser?.delete()?.await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthRecentLoginRequiredException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_RECENT_LOGIN_REQUIRED)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.UNKNOWN)

        }
    }
}