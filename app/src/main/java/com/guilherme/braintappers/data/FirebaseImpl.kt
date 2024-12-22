package com.guilherme.braintappers.data

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.guilherme.braintappers.domain.FirebaseError
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.Result
import kotlinx.coroutines.tasks.await

class FirebaseImpl : FirebaseRepository {

    override suspend fun currentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
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
    ): Result<Unit, FirebaseError> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(FirebaseError.FIREBASE_AUTH_USER_COLLISION)
        } catch (e: FirebaseNetworkException) {
            Result.Error(FirebaseError.FIREBASE_NETWORK)
        } catch (e: Exception) {
            println(e)
            Result.Error(FirebaseError.UNKNOWN)
        }

    }

    override suspend fun signUpWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//        Firebase.auth.signInWithCredential(firebaseCredential).await()

        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()

    }
}