package com.jnvst.guru.domain.model

data class ProgressResponse(
    val overall: ProgressSummary,
    val subjects: List<SubjectProgress>,
    val topics: List<TopicProgress>,
    val recentAttempts: List<RecentAttempt>
)

data class ProgressSummary(
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double
)

data class SubjectProgress(
    val subject: String,
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double
)

data class TopicProgress(
    val subject: String,
    val topic: String? = null,
    val topicId: Long? = null,
    val attempts: Long,
    val questions: Long,
    val correct: Long,
    val wrong: Long,
    val unanswered: Long,
    val score: Long,
    val accuracy: Double,
    val displayName: String? = null // Resolved in Repository
)

data class RecentAttempt(
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
    val submittedAt: String,
    val displayTitle: String? = null // Resolved in Repository
)
