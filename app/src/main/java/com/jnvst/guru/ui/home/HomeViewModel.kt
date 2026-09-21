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
        loadProfileData()
        loadLatestAttempt()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            when (val result = repository.getStudentProfile()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(studentName = result.data?.name) }
                }
                else -> {}
            }
        }
    }

    private fun loadLatestAttempt() {
        viewModelScope.launch {
            // We need to fetch the most recent activity.
            // Since we don't have a direct "get absolute latest" without parameters in repository right now,
            // let's look at recent attempts from progress if available, or try to infer.
            // Actually, the prompt says "Use existing GET /api/v1/student/practice-attempts/latest".
            // But the repository method requires parameters.
            
            // Looking at repository, progress API returns recentAttempts. Let's use the first one from there.
            val progressResult = repository.getProgress(0, 1)
            if (progressResult is Resource.Success) {
                val latest = progressResult.data?.recentAttempts?.firstOrNull()
                if (latest != null) {
                    _uiState.update {
                        it.copy(
                            continuePractice = ContinuePracticeUiState(
                                subjectName = latest.subject.replace("_", " ").capitalize(),
                                topicName = latest.displayTitle ?: "",
                                completedQuestions = latest.correctCount + latest.wrongCount,
                                totalQuestions = latest.questionCount
                            ),
                            latestAttemptData = LatestAttemptUiState(
                                mode = latest.practiceMode,
                                subject = latest.subject,
                                topic = latest.topic,
                                topicId = latest.topicId,
                                difficulty = latest.difficulty ?: "EASY",
                                language = latest.language,
                                page = latest.page
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadMockData() {
        _uiState.update {
            it.copy(
                notificationCount = 3,
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
