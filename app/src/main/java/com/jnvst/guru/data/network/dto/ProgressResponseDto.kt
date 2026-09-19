package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProgressResponseDto(
    val overall: ProgressSummaryDto,
    val subjects: List<SubjectProgressDto>,
    val topics: List<TopicProgressDto>,
    val recentAttempts: List<RecentAttemptDto>
)

@Serializable
data class ProgressSummaryDto(
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double
)

@Serializable
data class SubjectProgressDto(
    val subject: String,
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double
)

@Serializable
data class TopicProgressDto(
    val subject: String,
    val topic: String? = null,
    val topicId: Long? = null,
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double
)

@Serializable
data class RecentAttemptDto(
    val attemptId: Long,
    val practiceMode: String,
    val subject: String,
    val topic: String? = null,
    val topicId: Long? = null,
    val difficulty: String? = null,
    val language: String? = null,
    val page: Int,
    val score: Int,
    val questionCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val submittedAt: String
)
