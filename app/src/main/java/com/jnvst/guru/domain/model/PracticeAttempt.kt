package com.jnvst.guru.domain.model

data class PracticeAttempt(
    val attemptId: Long,
    val score: Int,
    val questionCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val submittedAt: String,
    val answers: List<PracticeAnswer>
)

data class PracticeAnswer(
    val questionId: Long,
    val selectedOption: String?,
    val correctOption: String?,
    val isCorrect: Boolean
)
