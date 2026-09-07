package com.jnvst.guru.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _uiState.update {
            it.copy(
                streakCount = 12,
                notificationCount = 3,
                continuePractice = ContinuePracticeUiState(
                    subjectName = "Mathematics",
                    topicName = "Algebraic Expressions",
                    completedQuestions = 12,
                    totalQuestions = 20
                ),
                progressSummary = ProgressSummaryUiState(
                    questionsSolved = 120,
                    averageAccuracy = 85,
                    studyTimeMinutes = 205, // 3h 25m
                    currentStreak = 12
                )
            )
        }
    }
}
