package com.jnvst.guru.ui.home

data class HomeUiState(
    val slogan: String = "",
    val streakCount: Int = 0,
    val notificationCount: Int = 0,
    val continuePractice: ContinuePracticeUiState? = null,
    val progressSummary: ProgressSummaryUiState = ProgressSummaryUiState(),
    val isLoading: Boolean = false
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
