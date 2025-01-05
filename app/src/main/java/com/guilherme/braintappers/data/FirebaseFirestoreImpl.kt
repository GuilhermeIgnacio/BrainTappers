package com.guilherme.braintappers.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseGetUserQuizzes
import com.guilherme.braintappers.domain.FirestoreError
import com.guilherme.braintappers.domain.Result
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

    /*
        override suspend fun getUserPlayedQuizzes(): Result<List<QuizResults>, FirebaseGetUserQuizzes> {
            val db = Firebase.firestore
            val currentUserUid = Firebase.auth.currentUser?.uid

            val foo: MutableList<QuizResults> = mutableListOf()

            val docRef =
                db.collection("users")
                    .document(currentUserUid.toString())
                    .collection("quizzesPlayed")

            return try {
                docRef.get()
                    .addOnSuccessListener { documents ->

                        for (document in documents) {

                            foo.add(
                                QuizResults(
                                    question = document.data["questions"] as List<String>,
                                    userAnswer = document.data["userAnswers"] as List<String>,
                                    correctAnswer = document.data["correctAnswers"] as List<String>
                                )
                            )

                        }

                    }
                    .addOnFailureListener {
                        println("Operation Failed $it")
                    }
                    .await()

                Result.Success(foo)
            } catch (e: Exception) {
                Result.Error(FirebaseGetUserQuizzes.UNKNOWN)
            }

        }
    */

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
                        correctAnswers = document.data?.get("correctAnswers") as List<String>
                    )
                }

            Result.Success(quizResults)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(FirebaseGetUserQuizzes.UNKNOWN)
        }

    }

}


data class QuizResults(
    val questions: List<String> = emptyList(),
    val userAnswers: List<String> = emptyList(),
    val correctAnswers: List<String> = emptyList()
)