package com.jnvst.guru.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.repository.PracticeRepositoryImpl
import com.jnvst.guru.domain.model.*
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PracticeMetadata(
    val mode: String,
    val subjectId: String,
    val topicType: String?,
    val difficulty: String,
    val page: Int
)

class PracticeViewModel(
    private val repository: PracticeRepository = PracticeRepositoryImpl()
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()
    
    private val _mockTests = MutableStateFlow<List<Test>>(emptyList())
    val mockTests: StateFlow<List<Test>> = _mockTests.asStateFlow()
    
    private val _selectedTest = MutableStateFlow<Test?>(null)
    val selectedTest: StateFlow<Test?> = _selectedTest.asStateFlow()

    private val _questions = MutableStateFlow<Resource<List<Question>>>(Resource.Loading())
    val questions: StateFlow<Resource<List<Question>>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _setStatuses = MutableStateFlow<Resource<List<SetStatus>>>(Resource.Loading())
    val setStatuses: StateFlow<Resource<List<SetStatus>>> = _setStatuses.asStateFlow()

    private val _latestAttempt = MutableStateFlow<Resource<PracticeAttempt>?>(null)
    val latestAttempt: StateFlow<Resource<PracticeAttempt>?> = _latestAttempt.asStateFlow()

    private val _isReviewMode = MutableStateFlow(false)
    val isReviewMode: StateFlow<Boolean> = _isReviewMode.asStateFlow()

    private val _submissionResult = MutableStateFlow<Resource<PracticeResult>?>(null)
    val submissionResult: StateFlow<Resource<PracticeResult>?> = _submissionResult.asStateFlow()

    private val _studentProfile = MutableStateFlow<Resource<StudentProfile>?>(null)
    val studentProfile: StateFlow<Resource<StudentProfile>?> = _studentProfile.asStateFlow()

    private var practiceMetadata: PracticeMetadata? = null

    private fun resetSessionState() {
        _questions.value = Resource.Loading()
        _submissionResult.value = null
        _latestAttempt.value = null
        _isReviewMode.value = false
    }

    init {
        loadSubjects()
        loadMockTests()
        loadStudentProfile()
    }

    fun loadStudentProfile() {
        viewModelScope.launch {
            _studentProfile.value = repository.getStudentProfile()
        }
    }

    fun startPractice(mode: String, subjectId: String, topicId: String?, difficulty: String, page: Int) {
        resetSessionState()
        _currentQuestionIndex.value = 0
        viewModelScope.launch {
            val type = when(topicId) {
                "analogy" -> "ANALOGY"
                "number_system" -> "NUMBER_SYSTEM"
                "fractions" -> "FRACTION"
                "decimals" -> "DECIMAL"
                else -> null
            }
            
            practiceMetadata = PracticeMetadata(mode, subjectId, type, difficulty, page)

            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = when (languageCode.lowercase()) {
                "bn" -> "BENGALI"
                else -> "ENGLISH"
            }

            val result = repository.getArithmeticQuestions(
                type = type,
                difficulty = difficulty,
                language = apiLanguage,
                page = page,
                size = 20
            )
            _questions.value = result
        }
    }

    fun nextQuestion() {
        val currentList = _questions.value.data ?: return
        if (_currentQuestionIndex.value < currentList.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun selectOption(index: Int) {
        val currentList = _questions.value.data?.toMutableList() ?: return
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx in currentList.indices) {
            currentList[currentIdx] = currentList[currentIdx].copy(selectedOptionIndex = index)
            _questions.value = Resource.Success(currentList)
        }
    }

    fun submitAttempt() {
        val metadata = practiceMetadata ?: return
        val currentQuestions = _questions.value.data ?: return

        viewModelScope.launch {
            _submissionResult.value = Resource.Loading()
            
            val answers = currentQuestions.map { 
                it.id to if (it.selectedOptionIndex == -1) null else it.selectedOptionIndex 
            }

            val result = repository.submitPracticeAttempt(
                mode = metadata.mode,
                subject = metadata.subjectId,
                topic = metadata.topicType,
                difficulty = metadata.difficulty,
                page = metadata.page,
                answers = answers
            )
            _submissionResult.value = result
        }
    }

    fun loadSetStatuses(mode: String, subjectId: String, topicId: String?, difficulty: String) {
        resetSessionState() // Synchronously reset any stale data
        viewModelScope.launch {
            _setStatuses.value = Resource.Loading()
            val type = when(topicId) {
                "analogy" -> "ANALOGY"
                "number_system" -> "NUMBER_SYSTEM"
                "fractions" -> "FRACTION"
                "decimals" -> "DECIMAL"
                else -> null
            }
            _setStatuses.value = repository.getPracticeStatus(mode, subjectId, type, difficulty)
        }
    }

    fun loadLatestAttempt(mode: String, subjectId: String, topicId: String?, difficulty: String, page: Int) {
        _submissionResult.value = null // Clear fresh submission when viewing history
        _latestAttempt.value = Resource.Loading()
        _isReviewMode.value = false
        
        viewModelScope.launch {
            val type = when(topicId) {
                "analogy" -> "ANALOGY"
                "number_system" -> "NUMBER_SYSTEM"
                "fractions" -> "FRACTION"
                "decimals" -> "DECIMAL"
                else -> null
            }
            _latestAttempt.value = repository.getLatestAttempt(mode, subjectId, type, difficulty, page)
        }
    }

    fun enterReviewMode(mode: String, subjectId: String, topicId: String?, difficulty: String, page: Int) {
        _submissionResult.value = null
        _questions.value = Resource.Loading()
        _isReviewMode.value = true
        _currentQuestionIndex.value = 0
        
        viewModelScope.launch {
            // 1. Load questions first
            val type = when(topicId) {
                "analogy" -> "ANALOGY"
                "number_system" -> "NUMBER_SYSTEM"
                "fractions" -> "FRACTION"
                "decimals" -> "DECIMAL"
                else -> null
            }
            
            // Sync metadata
            practiceMetadata = PracticeMetadata(mode, subjectId, type, difficulty, page)
            
            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = when (languageCode.lowercase()) {
                "bn" -> "BENGALI"
                else -> "ENGLISH"
            }

            val qResult = repository.getArithmeticQuestions(type, difficulty, apiLanguage, page, 20)
            
            // 2. Load latest attempt if not already loaded or different
            if (_latestAttempt.value?.data == null) {
                _latestAttempt.value = repository.getLatestAttempt(mode, subjectId, type, difficulty, page)
            }
            
            val attempt = _latestAttempt.value?.data
            val currentQuestions = qResult.data
            
            if (attempt != null && currentQuestions != null) {
                val updatedQuestions = currentQuestions.map { question ->
                    val answer = attempt.answers.find { it.questionId == question.id }
                    val selectedIdx = when (answer?.selectedOption) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> -1
                    }
                    val correctIdx = when (answer?.correctOption) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> -1
                    }
                    question.copy(selectedOptionIndex = selectedIdx, correctOptionIndex = correctIdx)
                }
                _questions.value = Resource.Success(updatedQuestions)
            } else {
                _questions.value = qResult
            }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            repository.getSubjects().collect {
                _subjects.value = it
            }
        }
    }
    
    private fun loadMockTests() {
        viewModelScope.launch {
            repository.getMockTests().collect {
                _mockTests.value = it
            }
        }
    }

    fun loadTopics(subjectId: String) {
        viewModelScope.launch {
            repository.getTopicsForSubject(subjectId).collect {
                _topics.value = it
            }
            repository.getSubjectById(subjectId).collect {
                _selectedSubject.value = it
            }
        }
    }
    
    fun loadTestDetail(testId: String) {
        viewModelScope.launch {
            repository.getTestById(testId).collect {
                _selectedTest.value = it
            }
        }
    }
}
