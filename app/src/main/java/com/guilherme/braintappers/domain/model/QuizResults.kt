package com.guilherme.braintappers.domain.model

import com.google.firebase.Timestamp

data class QuizResults(
    val questions: List<String> = emptyList(),
    val userAnswers: List<String> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val category: String? = null,
    val createdAt: Timestamp? = null
)