package com.guilherme.braintappers.data

import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.guilherme.braintappers.domain.FirebaseRepository
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

    override suspend fun signUpWithEmail(email: String, password: String) {
        createAnonymousAccount()

        val credential = EmailAuthProvider.getCredential(email, password)
        Firebase.auth.currentUser!!.linkWithCredential(credential) //⚠️This could trigger some nullability errors

    }

    override suspend fun signUpWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//        Firebase.auth.signInWithCredential(firebaseCredential).await()

        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()

    }
}