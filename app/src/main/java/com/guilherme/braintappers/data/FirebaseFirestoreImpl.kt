package com.guilherme.braintappers.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirestoreError
import com.guilherme.braintappers.domain.Result
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreImpl : FirebaseFirestoreRepository {

    override suspend fun write(
        quizUid: String,
        data: HashMap<String, List<String>>
    ): Result<Unit, FirestoreError> {

        return try {
            val db = Firebase.firestore
            val currentUserUid = Firebase.auth.currentUser?.uid

            db.document("users/$currentUserUid/quizzesPlayed/${quizUid}").set(data).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(FirestoreError.UNKNOWN)
        }

    }


}