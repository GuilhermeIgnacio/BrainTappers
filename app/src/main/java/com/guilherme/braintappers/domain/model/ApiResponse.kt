package com.guilherme.braintappers.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("response_code")
    val responseCode: Int,
    @SerialName("results")
    val results: List<Question>
)

@Serializable
data class Question(
    @SerialName("category")
    val category: String,

    @SerialName("correct_answer")
    val correctAnswer: String,

    @SerialName("difficulty")
    val difficulty: String,

    @SerialName("incorrect_answers")
    val incorrectAnswers: List<String>,

    @SerialName("question")
    val question: String,

    @SerialName("type")
    val type: String
)