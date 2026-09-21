package com.jnvst.guru.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.repository.PracticeRepositoryImpl
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PracticeRepository = PracticeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
        loadRealProgressData()
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
                    setsCompleted = 4,
                    topicsPracticed = 2
                )
            )
        }
    }

    private fun loadRealProgressData() {
        viewModelScope.launch {
            when (val result = repository.getProgress(0, 100)) {
                is Resource.Success -> {
                    val progressData = result.data
                    if (progressData != null) {
                        val qSolved = progressData.overall.questions.toInt()
                        val avgAccuracy = progressData.overall.accuracy.toInt()

                        val uniqueSets = progressData.recentAttempts.map { attempt ->
                            "${attempt.subject}_${attempt.topicId}_${attempt.topic}_${attempt.difficulty}_${attempt.language}_${attempt.page}"
                        }.distinct().size

                        val uniqueTopics = progressData.topics.filter { it.attempts > 0 && (it.topicId != null || it.topic != null) }
                            .map { it.topicId?.toString() ?: it.topic.orEmpty() }
                            .distinct()
                            .size

                        _uiState.update {
                            it.copy(
                                progressSummary = ProgressSummaryUiState(
                                    questionsSolved = qSolved,
                                    averageAccuracy = avgAccuracy,
                                    setsCompleted = uniqueSets,
                                    topicsPracticed = uniqueTopics
                                )
                            )
                        }
                    }
                }
                else -> {
                    // Retain defaults/mock if failed or loading
                }
            }
        }
    }
}
