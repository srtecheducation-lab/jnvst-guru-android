package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PracticeAttemptRequestDto(
    val practiceMode: String,
    val subject: String,
    val topic: String?,
    val topicId: Long? = null,
    val difficulty: String,
    val page: Int,
    val answers: List<AnswerRequestDto>
)

@Serializable
data class AnswerRequestDto(
    val questionId: Long,
    val selectedOption: String?
)
