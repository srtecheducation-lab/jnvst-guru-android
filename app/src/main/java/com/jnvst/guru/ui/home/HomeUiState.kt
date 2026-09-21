package com.jnvst.guru.ui.home

data class HomeUiState(
    val notificationCount: Int = 0,
    val studentName: String? = null,
    val continuePractice: ContinuePracticeUiState? = null,
    val latestAttemptData: LatestAttemptUiState? = null,
    val progressSummary: ProgressSummaryUiState = ProgressSummaryUiState(),
    val isLoading: Boolean = false
)

data class LatestAttemptUiState(
    val mode: String,
    val subject: String,
    val topic: String?,
    val topicId: Long?,
    val difficulty: String,
    val language: String?,
    val page: Int
)

data class ContinuePracticeUiState(
    val subjectName: String,
    val topicName: String,
    val completedQuestions: Int,
    val totalQuestions: Int,
    val subjectIconRes: Int? = null
)

data class ProgressSummaryUiState(
    val questionsSolved: Int = 0,
    val averageAccuracy: Int = 0,
    val setsCompleted: Int = 0,
    val topicsPracticed: Int = 0
)
