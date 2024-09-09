package com.guilherme.braintappers.data

import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.guilherme.braintappers.domain.FirebaseRepository
import kotlinx.coroutines.tasks.await

class FirebaseImpl : FirebaseRepository {

    override suspend fun signUpWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//        Firebase.auth.signInWithCredential(firebaseCredential).await()

        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()

    }
}