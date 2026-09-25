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
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            when (val result = repository.getStudentProfile(forceRefresh = false)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(studentName = result.data?.name) }
                }
                else -> {}
            }

            when (val result = repository.getProgress(0, 20)) {
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

                        val latest = progressData.recentAttempts.firstOrNull()

                        _uiState.update {
                            it.copy(
                                notificationCount = 3,
                                progressSummary = ProgressSummaryUiState(
                                    questionsSolved = qSolved,
                                    averageAccuracy = avgAccuracy,
                                    setsCompleted = uniqueSets,
                                    topicsPracticed = uniqueTopics
                                ),
                                continuePractice = if (latest != null) {
                                    ContinuePracticeUiState(
                                        subjectName = latest.displaySubject ?: latest.subject.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() },
                                        topicName = latest.displayTopic ?: latest.displaySubtitle ?: "",
                                        completedQuestions = latest.correctCount + latest.wrongCount,
                                        totalQuestions = latest.questionCount
                                    )
                                } else it.continuePractice,
                                latestAttemptData = if (latest != null) {
                                    LatestAttemptUiState(
                                        mode = latest.practiceMode,
                                        subject = latest.subject,
                                        topic = latest.topic,
                                        topicId = latest.topicId,
                                        difficulty = latest.difficulty ?: "EASY",
                                        language = latest.language,
                                        page = latest.page
                                    )
                                } else it.latestAttemptData
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
