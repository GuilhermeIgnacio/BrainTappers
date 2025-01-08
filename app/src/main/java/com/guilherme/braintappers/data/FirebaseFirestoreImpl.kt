package com.guilherme.braintappers.data

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.guilherme.braintappers.domain.FirebaseFirestoreDeleteError
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseGetUserQuizzes
import com.guilherme.braintappers.domain.FirestoreError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.model.QuizResults
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreImpl : FirebaseFirestoreRepository {

    override suspend fun write(
        quizUid: String,
        data: HashMap<String, Any?>
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

    override suspend fun getUserPlayedQuizzes(): Result<List<QuizResults>, FirebaseGetUserQuizzes> {

        val db = Firebase.firestore
        val currentUserUid = Firebase.auth.currentUser?.uid

        return try {

            val quizResults = db.collection("users")
                .document(currentUserUid.toString())
                .collection("quizzesPlayed")
                .get()
                .await().documents.map { document ->
                    QuizResults(
                        questions = document.data?.get("questions") as List<String>,
                        userAnswers = document.data?.get("userAnswers") as List<String>,
                        correctAnswers = document.data?.get("correctAnswers") as List<String>,
                        category = document.data?.get("category") as String?,
                        createdAt = document.data?.get("createdAt") as Timestamp?
                    )
                }

            Result.Success(quizResults)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(FirebaseGetUserQuizzes.UNKNOWN)
        }

    }

    override suspend fun deleteData(): Result<Unit, FirebaseFirestoreDeleteError> {

        val db = Firebase.firestore
        val currentUserUid = Firebase.auth.currentUser?.uid.toString()

        val documentPath = db.collection("users")
            .document(currentUserUid)
            .collection("quizzesPlayed")

        return try {

            val documentsIds = documentPath.get().await().documents.map { it.id }

            documentsIds.forEach { documentPath.document(it).delete().await() }

            Firebase.firestore.clearPersistence().await()

            Result.Success(Unit)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseFirestoreDeleteError.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseFirestoreDeleteError.UNKNOWN)

        }


    }

}