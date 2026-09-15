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

    private val _sessionItems = MutableStateFlow<Resource<List<PracticeSessionItem>>>(Resource.Loading())
    val sessionItems: StateFlow<Resource<List<PracticeSessionItem>>> = _sessionItems.asStateFlow()

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

    private val _matTopicsMetadata = MutableStateFlow<List<Topic>>(emptyList())
    val matTopicsMetadata: StateFlow<List<Topic>> = _matTopicsMetadata.asStateFlow()

    private val _currentTopic = MutableStateFlow<Topic?>(null)
    val currentTopic: StateFlow<Topic?> = _currentTopic.asStateFlow()

    private var practiceMetadata: PracticeMetadata? = null

    private fun resetSessionState() {
        _questions.value = Resource.Loading()
        _sessionItems.value = Resource.Loading()
        _submissionResult.value = null
        _latestAttempt.value = null
        _isReviewMode.value = false
        _currentTopic.value = null
    }

    init {
        loadSubjects()
        loadMockTests()
        loadStudentProfile()
        loadMatTopicsMetadata()
    }

    private fun loadMatTopicsMetadata() {
        viewModelScope.launch {
            repository.getTopicsForSubject("mat").collect {
                _matTopicsMetadata.value = it
            }
        }
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
            if (subjectId.equals("mat", ignoreCase = true)) {
                // Ensure metadata is loaded if it's the first time
                if (_matTopicsMetadata.value.isEmpty()) {
                    repository.getTopicsForSubject("mat").collect {
                        _matTopicsMetadata.value = it
                    }
                }
                _currentTopic.value = _matTopicsMetadata.value.find { it.id == topicId }
                practiceMetadata = PracticeMetadata(mode, subjectId, topicId, difficulty, page)
                val result = repository.getMatQuestions(
                    topicId = topicId?.toLongOrNull(),
                    difficulty = difficulty,
                    page = page,
                    size = 20
                )
                _questions.value = result
                // Also populate sessionItems for unified UI
                if (result is Resource.Success) {
                    _sessionItems.value = Resource.Success(result.data!!.map { PracticeSessionItem.QuestionItem(it) })
                }
            } else if (subjectId.equals("language", ignoreCase = true)) {
                practiceMetadata = PracticeMetadata(mode, subjectId, topicId, difficulty, page)
                val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
                val apiLanguage = if (languageCode.lowercase() == "bn") "BENGALI" else "ENGLISH"
                
                val result = repository.getLanguageQuestions(apiLanguage, page, 4)
                if (result is Resource.Success) {
                    val passages = result.data ?: emptyList()
                    val items = mutableListOf<PracticeSessionItem>()
                    passages.forEach { passage ->
                        items.add(PracticeSessionItem.PassageItem(passage.id, passage.number, passage.text, passages.size))
                        passage.questions.forEach { q ->
                            items.add(PracticeSessionItem.QuestionItem(q, passage.id, passage.number))
                        }
                    }
                    _sessionItems.value = Resource.Success(items)
                    // Also populate _questions for submission logic compatibility
                    val allQuestions = passages.flatMap { it.questions }
                    _questions.value = Resource.Success(allQuestions)
                } else {
                    _sessionItems.value = Resource.Error(result.message ?: "Error")
                    _questions.value = Resource.Error(result.message ?: "Error")
                }
            } else {
                val type = when(topicId) {
                    "analogy" -> "ANALOGY"
                    "number_system" -> "NUMBER_SYSTEM"
                    "fractions" -> "FRACTION"
                    "decimals" -> "DECIMAL"
                    else -> null
                }
                
                _currentTopic.value = _topics.value.find { it.id == topicId }
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
                // Also populate sessionItems for unified UI
                if (result is Resource.Success) {
                    _sessionItems.value = Resource.Success(result.data!!.map { PracticeSessionItem.QuestionItem(it) })
                }
            }
        }
    }

    fun nextQuestion() {
        val currentItems = _sessionItems.value.data ?: return
        if (_currentQuestionIndex.value < currentItems.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun goToPassage() {
        val currentItems = _sessionItems.value.data ?: return
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx in currentItems.indices) {
            val item = currentItems[currentIdx]
            if (item is PracticeSessionItem.QuestionItem) {
                // Find the nearest previous PassageItem
                val passageIdx = currentItems.subList(0, currentIdx).indexOfLast { it is PracticeSessionItem.PassageItem }
                if (passageIdx != -1) {
                    _currentQuestionIndex.value = passageIdx
                }
            }
        }
    }

    fun selectOption(index: Int) {
        val currentItems = _sessionItems.value.data?.toMutableList() ?: return
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx in currentItems.indices) {
            val item = currentItems[currentIdx]
            if (item is PracticeSessionItem.QuestionItem) {
                val updatedQuestion = item.question.copy(selectedOptionIndex = index)
                currentItems[currentIdx] = item.copy(question = updatedQuestion)
                _sessionItems.value = Resource.Success(currentItems)
                
                // Sync with _questions for submission logic
                val allQuestions = _questions.value.data?.toMutableList() ?: return
                val qIdx = allQuestions.indexOfFirst { it.id == updatedQuestion.id }
                if (qIdx != -1) {
                    allQuestions[qIdx] = updatedQuestion
                    _questions.value = Resource.Success(allQuestions)
                }
            }
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

            val (topic, tId) = if (metadata.subjectId.equals("mat", ignoreCase = true)) {
                null to metadata.topicType?.toLongOrNull()
            } else {
                metadata.topicType to null
            }

            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = if (languageCode.lowercase() == "bn") "BENGALI" else "ENGLISH"

            val result = repository.submitPracticeAttempt(
                mode = metadata.mode,
                subject = metadata.subjectId,
                topic = topic,
                topicId = tId,
                difficulty = metadata.difficulty,
                language = apiLanguage,
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
            val (type, tId) = if (subjectId.equals("mat", ignoreCase = true)) {
                null to topicId?.toLongOrNull()
            } else if (subjectId.equals("language", ignoreCase = true)) {
                null to null
            } else {
                val type = when(topicId) {
                    "analogy" -> "ANALOGY"
                    "number_system" -> "NUMBER_SYSTEM"
                    "fractions" -> "FRACTION"
                    "decimals" -> "DECIMAL"
                    else -> null
                }
                type to null
            }
            
            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = if (languageCode.lowercase() == "bn") "BENGALI" else "ENGLISH"

            _setStatuses.value = repository.getPracticeStatus(mode, subjectId, type, tId, difficulty, apiLanguage)
        }
    }

    fun loadLatestAttempt(mode: String, subjectId: String, topicId: String?, difficulty: String, page: Int) {
        _submissionResult.value = null // Clear fresh submission when viewing history
        _latestAttempt.value = Resource.Loading()
        _isReviewMode.value = false
        
        viewModelScope.launch {
            val (type, tId) = if (subjectId.equals("mat", ignoreCase = true)) {
                null to topicId?.toLongOrNull()
            } else {
                val type = when(topicId) {
                    "analogy" -> "ANALOGY"
                    "number_system" -> "NUMBER_SYSTEM"
                    "fractions" -> "FRACTION"
                    "decimals" -> "DECIMAL"
                    else -> null
                }
                type to null
            }
            
            // Check if Language to add language parameter?
            // Existing repository.getLatestAttempt:
            // suspend fun getLatestAttempt(mode: String, subject: String, topic: String?, topicId: Long?, difficulty: String, page: Int): Resource<PracticeAttempt>
            // I should update it to support language.
            
            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = if (languageCode.lowercase() == "bn") "BENGALI" else "ENGLISH"

            _latestAttempt.value = repository.getLatestAttempt(mode, subjectId, type, tId, difficulty, apiLanguage, page)
        }
    }

    fun enterReviewMode(mode: String, subjectId: String, topicId: String?, difficulty: String, page: Int) {
        _submissionResult.value = null
        _questions.value = Resource.Loading()
        _sessionItems.value = Resource.Loading()
        _isReviewMode.value = true
        _currentQuestionIndex.value = 0
        
        viewModelScope.launch {
            val languageCode = _studentProfile.value?.data?.preferredLanguage ?: "en"
            val apiLanguage = if (languageCode.lowercase() == "bn") "BENGALI" else "ENGLISH"

            // Ensure metadata is loaded for MAT
            if (subjectId.equals("mat", ignoreCase = true) && _matTopicsMetadata.value.isEmpty()) {
                repository.getTopicsForSubject("mat").collect {
                    _matTopicsMetadata.value = it
                }
            }

            // 1. Load questions first
            val type = if (subjectId.equals("mat", ignoreCase = true)) {
                topicId
            } else {
                when(topicId) {
                    "analogy" -> "ANALOGY"
                    "number_system" -> "NUMBER_SYSTEM"
                    "fractions" -> "FRACTION"
                    "decimals" -> "DECIMAL"
                    else -> null
                }
            }
            
            // Sync metadata
            practiceMetadata = PracticeMetadata(mode, subjectId, type, difficulty, page)
            
            if (subjectId.equals("language", ignoreCase = true)) {
                val result = repository.getLanguageQuestions(apiLanguage, page, 4)
                if (result is Resource.Success) {
                    val passages = result.data ?: emptyList()
                    val items = mutableListOf<PracticeSessionItem>()
                    passages.forEach { passage ->
                        items.add(PracticeSessionItem.PassageItem(passage.id, passage.number, passage.text, passages.size))
                        passage.questions.forEach { q ->
                            items.add(PracticeSessionItem.QuestionItem(q, passage.id, passage.number))
                        }
                    }
                    _sessionItems.value = Resource.Success(items)
                    val allQuestions = passages.flatMap { it.questions }
                    _questions.value = Resource.Success(allQuestions)
                }
            } else if (subjectId.equals("mat", ignoreCase = true)) {
                val result = repository.getMatQuestions(topicId?.toLongOrNull(), difficulty, page, 20)
                _questions.value = result
                if (result is Resource.Success) {
                    _sessionItems.value = Resource.Success(result.data!!.map { PracticeSessionItem.QuestionItem(it) })
                }
            } else {
                val result = repository.getArithmeticQuestions(type, difficulty, apiLanguage, page, 20)
                _questions.value = result
                if (result is Resource.Success) {
                    _sessionItems.value = Resource.Success(result.data!!.map { PracticeSessionItem.QuestionItem(it) })
                }
            }
            
            // 2. Load latest attempt if not already loaded or different
            if (_latestAttempt.value?.data == null) {
                val tId = if (subjectId.equals("mat", ignoreCase = true)) topicId?.toLongOrNull() else null
                _latestAttempt.value = repository.getLatestAttempt(mode, subjectId, type, tId, difficulty, apiLanguage, page)
            }
            
            val attempt = _latestAttempt.value?.data
            val currentQuestions = _questions.value.data
            
            if (attempt != null && currentQuestions != null) {
                // Update selections based on attempt
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
                
                // Also update sessionItems
                val currentItems = _sessionItems.value.data?.toMutableList()
                if (currentItems != null) {
                    currentItems.forEachIndexed { idx, item ->
                        if (item is PracticeSessionItem.QuestionItem) {
                            val updatedQ = updatedQuestions.find { it.id == item.question.id }
                            if (updatedQ != null) {
                                currentItems[idx] = item.copy(question = updatedQ)
                            }
                        }
                    }
                    _sessionItems.value = Resource.Success(currentItems)
                }
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
                if (subjectId.equals("mat", ignoreCase = true)) {
                    _matTopicsMetadata.value = it
                }
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
