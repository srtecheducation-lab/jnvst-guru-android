package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PracticeAttemptResponseDto(
    val attemptId: Long,
    val practiceMode: String,
    val subject: String,
    val topic: String?,
    val difficulty: String? = null,
    val page: Int,
    val score: Int,
    val questionCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val submittedAt: String,
    val answers: List<AnswerResponseDto> = emptyList()
)

@Serializable
data class AnswerResponseDto(
    val questionId: Long? = null,
    val matQuestionId: Long? = null,
    val selectedOption: String?,
    val correctOption: String?,
    val isCorrect: Boolean
)
