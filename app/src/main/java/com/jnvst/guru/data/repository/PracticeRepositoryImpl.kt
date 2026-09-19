package com.jnvst.guru.data.repository

import com.jnvst.guru.R
import com.jnvst.guru.data.network.NetworkModule
import com.jnvst.guru.data.network.dto.AnswerRequestDto
import com.jnvst.guru.data.network.dto.PracticeAttemptRequestDto
import com.jnvst.guru.data.network.dto.LanguageQuestionDto
import com.jnvst.guru.data.network.dto.MatQuestionDto
import com.jnvst.guru.data.network.dto.MatTopicDto
import com.jnvst.guru.data.network.dto.StudentLanguagePassageResponseDto
import com.jnvst.guru.data.network.dto.StudentProfileRequestDto
import com.jnvst.guru.data.network.util.MatImageUrlBuilder
import com.jnvst.guru.domain.model.*
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class PracticeRepositoryImpl : PracticeRepository {

    private var cachedMatTopics: List<Topic> = emptyList()

    private val subjects = listOf(
        Subject(
            id = "mat",
            nameResId = R.string.subject_mat,
            descriptionResId = R.string.subject_mat_desc,
            iconResId = R.drawable.ic_launcher_foreground,
            topicCount = 10,
            colorHex = "#9C27B0"
        ),
        Subject(
            id = "arithmetic",
            nameResId = R.string.subject_arithmetic,
            descriptionResId = R.string.subject_arithmetic_desc,
            iconResId = R.drawable.ic_launcher_foreground,
            topicCount = 10,
            colorHex = "#FF9800"
        ),
        Subject(
            id = "language",
            nameResId = R.string.subject_language,
            descriptionResId = R.string.subject_language_desc,
            iconResId = R.drawable.ic_launcher_foreground,
            topicCount = 8,
            colorHex = "#4CAF50"
        )
    )

    private val topics = mapOf(
        "mental_ability" to listOf(
            Topic("analogy", "mental_ability", nameResId = R.string.topic_analogy, questionCount = 20, progress = 85, durationMinutes = 30, difficultyResId = R.string.difficulty_medium),
            Topic("classification", "mental_ability", nameResId = R.string.topic_classification, questionCount = 25, progress = 72, durationMinutes = 35, difficultyResId = R.string.difficulty_hard),
            Topic("figure_series", "mental_ability", nameResId = R.string.topic_figure_series, questionCount = 30, progress = 54, durationMinutes = 40, difficultyResId = R.string.difficulty_easy),
            Topic("direction_sense", "mental_ability", nameResId = R.string.topic_grammar, questionCount = 15, progress = 91, durationMinutes = 20, difficultyResId = R.string.difficulty_medium),
            Topic("mirror_images", "mental_ability", nameResId = R.string.topic_grammar, questionCount = 20, progress = 68, durationMinutes = 25, difficultyResId = R.string.difficulty_hard)
        ),
        "arithmetic" to listOf(
            Topic("number_system", "arithmetic", nameResId = R.string.topic_number_system, questionCount = 40, progress = 45, durationMinutes = 60, difficultyResId = R.string.difficulty_hard),
            Topic("fractions", "arithmetic", nameResId = R.string.topic_fractions, questionCount = 30, progress = 60, durationMinutes = 45, difficultyResId = R.string.difficulty_medium),
            Topic("decimals", "arithmetic", nameResId = R.string.topic_decimals, questionCount = 25, progress = 75, durationMinutes = 30, difficultyResId = R.string.difficulty_easy)
        ),
        "language" to listOf(
            Topic("reading_comp", "language", nameResId = R.string.topic_reading_comprehension, questionCount = 10, progress = 30, durationMinutes = 20, difficultyResId = R.string.difficulty_medium),
            Topic("vocabulary", "language", nameResId = R.string.topic_vocabulary, questionCount = 50, progress = 20, durationMinutes = 40, difficultyResId = R.string.difficulty_easy),
            Topic("grammar", "language", nameResId = R.string.topic_grammar, questionCount = 100, progress = 10, durationMinutes = 90, difficultyResId = R.string.difficulty_hard)
        )
    )

    private val mockTests = listOf(
        Test("mock_1", "JNVST Mock Test 1", 100, 120),
        Test("mock_2", "JNVST Mock Test 2", 100, 120, isLocked = true),
        Test("mock_3", "JNVST Mock Test 3", 100, 120, isLocked = true),
        Test("mock_4", "JNVST Mock Test 4", 100, 120, isLocked = true),
        Test("mock_5", "JNVST Mock Test 5", 100, 120, isLocked = true)
    )

    override fun getSubjects(): Flow<List<Subject>> = flowOf(subjects)

    override fun getTopicsForSubject(subjectId: String): Flow<List<Topic>> = flow {
        if (subjectId.equals("mat", ignoreCase = true)) {
            try {
                val response = NetworkModule.arithmeticService.getMatTopics()
                val topics = response.content.sortedBy { it.sortOrder }.map { dto ->
                    Topic(
                        id = dto.id.toString(),
                        subjectId = "mat",
                        code = dto.code,
                        nameOverride = dto.name,
                        descriptionOverride = dto.description,
                        questionCount = dto.questionCount,
                        durationMinutes = 30
                    )
                }
                cachedMatTopics = topics
                emit(topics)
            } catch (e: Exception) {
                android.util.Log.e("PracticeRepo", "Error fetching MAT topics", e)
                emit(emptyList())
            }
        } else if (subjectId.equals("language", ignoreCase = true)) {
            // Hard-coded "Passage" topic for Language as per requirement
            emit(listOf(
                Topic(
                    id = "passage",
                    subjectId = "language",
                    nameResId = R.string.topic_reading_comprehension, // Use existing string for Reading Comprehension/Passage
                    questionCount = 100, // Placeholder
                    durationMinutes = 60
                )
            ))
        } else {
            emit(topics[subjectId] ?: emptyList())
        }
    }

    override fun getSubjectById(subjectId: String): Flow<Subject?> = 
        flowOf(subjects.find { it.id == subjectId })

    override fun getMockTests(): Flow<List<Test>> = flowOf(mockTests)

    override fun getTestById(testId: String): Flow<Test?> = 
        flowOf(mockTests.find { it.id == testId })

    override suspend fun getArithmeticQuestions(
        type: String?,
        difficulty: String?,
        language: String?,
        page: Int,
        size: Int
    ): Resource<List<Question>> {
        return try {
            val response = NetworkModule.arithmeticService.getArithmeticQuestions(type, difficulty, language, page, size)
            val questions = response.content
                .filter { it.questionText != null && it.optionA != null } // Skip incomplete/null questions
                .map { dto ->
                    Question(
                        id = dto.id,
                        questionType = dto.questionType,
                        questionText = dto.questionText!!,
                        options = listOf(
                            dto.optionA!!,
                            dto.optionB ?: "",
                            dto.optionC ?: "",
                            dto.optionD ?: ""
                        ),
                        difficulty = dto.difficulty
                    )
                }
            Resource.Success(questions)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch questions")
        }
    }

    override suspend fun getMatTopics(): Resource<List<Topic>> {
        return try {
            val response = NetworkModule.arithmeticService.getMatTopics()
            val topics = response.content.sortedBy { it.sortOrder }.map { dto ->
                Topic(
                    id = dto.id.toString(),
                    subjectId = "mat",
                    code = dto.code,
                    nameOverride = dto.name,
                    descriptionOverride = dto.description,
                    questionCount = dto.questionCount,
                    durationMinutes = 30
                )
            }
            cachedMatTopics = topics
            Resource.Success(topics)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch MAT topics")
        }
    }

    override suspend fun getMatQuestions(
        topicId: Long?,
        difficulty: String?,
        page: Int,
        size: Int
    ): Resource<List<Question>> {
        return try {
            // Ensure MAT topics are cached for ID -> Code mapping
            if (cachedMatTopics.isEmpty()) {
                val response = NetworkModule.arithmeticService.getMatTopics()
                cachedMatTopics = response.content.map { dto ->
                    Topic(
                        id = dto.id.toString(),
                        subjectId = "mat",
                        code = dto.code,
                        nameOverride = dto.name,
                        descriptionOverride = dto.description,
                        questionCount = dto.questionCount
                    )
                }
            }

            val response = NetworkModule.arithmeticService.getMatQuestions(topicId, difficulty, page, size)
            val questions = response.content.map { dto ->
                val topicCode = cachedMatTopics.find { it.id == dto.topicId.toString() }?.code
                Question(
                    id = dto.id,
                    questionType = "MAT",
                    topicCode = topicCode,
                    questionImageUrl = MatImageUrlBuilder.buildUrl(dto.questionImageUrl),
                    optionImageUrls = listOf(
                        MatImageUrlBuilder.buildUrl(dto.optionAImageUrl) ?: "",
                        MatImageUrlBuilder.buildUrl(dto.optionBImageUrl) ?: "",
                        MatImageUrlBuilder.buildUrl(dto.optionCImageUrl) ?: "",
                        MatImageUrlBuilder.buildUrl(dto.optionDImageUrl) ?: ""
                    ),
                    difficulty = dto.difficulty
                )
            }
            Resource.Success(questions)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch MAT questions")
        }
    }

    override suspend fun getLanguageQuestions(
        language: String,
        page: Int,
        size: Int
    ): Resource<List<LanguagePassage>> {
        return try {
            val response = NetworkModule.arithmeticService.getLanguageQuestions(language.uppercase(), page, size)
            val passages = response.content
                .filter { it.passageText != null } // Skip empty passages
                .mapIndexed { index, passageDto ->
                    LanguagePassage(
                        id = passageDto.passageId,
                        number = index + 1, // Reset to 1-4 based on position in current page
                        text = passageDto.passageText!!,
                        questions = passageDto.questions
                            .filter { it.questionText != null && it.optionA != null }
                            .map { qDto ->
                                Question(
                                    id = qDto.questionId,
                                    questionType = "LANGUAGE",
                                    questionText = qDto.questionText!!,
                                    options = listOf(
                                        qDto.optionA!!,
                                        qDto.optionB ?: "",
                                        qDto.optionC ?: "",
                                        qDto.optionD ?: ""
                                    ),
                                    difficulty = ""
                                )
                            }
                    )
                }
            Resource.Success(passages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch Language questions")
        }
    }

    override suspend fun submitPracticeAttempt(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?,
        page: Int,
        answers: List<Pair<Long, Int?>>
    ): Resource<PracticeResult> {
        return try {
            val answerDtos = answers.map { (qId, optionIdx) ->
                val optionChar = when (optionIdx) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    3 -> "D"
                    else -> null
                }
                AnswerRequestDto(qId, optionChar)
            }
            val request = PracticeAttemptRequestDto(
                practiceMode = if (subject.lowercase() == "language") "SUBJECT" else mode.uppercase(),
                subject = subject.uppercase(),
                topic = if (subject.lowercase() == "mat" || subject.lowercase() == "language") null else topic?.uppercase(),
                topicId = topicId,
                difficulty = if (subject.lowercase() == "language") null else difficulty.uppercase(),
                language = language?.uppercase(),
                page = page,
                answers = answerDtos
            )
            val response = NetworkModule.arithmeticService.submitPracticeAttempt(request)
            val result = PracticeResult(
                score = response.score,
                questionCount = response.questionCount,
                correctCount = response.correctCount,
                wrongCount = response.wrongCount,
                unansweredCount = response.unansweredCount
            )
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit attempt")
        }
    }

    override suspend fun getPracticeStatus(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?
    ): Resource<List<SetStatus>> {
        return try {
            val response = NetworkModule.arithmeticService.getPracticeStatus(
                practiceMode = if (subject.lowercase() == "language") "SUBJECT" else mode.uppercase(),
                subject = subject.uppercase(),
                topic = if (subject.lowercase() == "mat" || subject.lowercase() == "language") null else topic?.uppercase(),
                topicId = topicId,
                difficulty = if (subject.lowercase() == "language") null else difficulty.uppercase(),
                language = language?.uppercase()
            )
            val sets = response.sets.map { dto ->
                SetStatus(
                    page = dto.page,
                    setNumber = dto.setNumber,
                    questionCount = dto.questionCount,
                    completed = dto.completed
                )
            }
            Resource.Success(sets)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch status")
        }
    }

    override suspend fun getLatestAttempt(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?,
        page: Int
    ): Resource<PracticeAttempt> {
        return try {
            val response = NetworkModule.arithmeticService.getLatestAttempt(
                practiceMode = if (subject.lowercase() == "language") "SUBJECT" else mode.uppercase(),
                subject = subject.uppercase(),
                topic = if (subject.lowercase() == "mat" || subject.lowercase() == "language") null else topic?.uppercase(),
                topicId = topicId,
                difficulty = if (subject.lowercase() == "language") null else difficulty.uppercase(),
                language = language?.uppercase(),
                page = page
            )
            val attempt = PracticeAttempt(
                attemptId = response.attemptId,
                score = response.score,
                questionCount = response.questionCount,
                correctCount = response.correctCount,
                wrongCount = response.wrongCount,
                unansweredCount = response.unansweredCount,
                submittedAt = response.submittedAt,
                answers = response.answers.map {
                    PracticeAnswer(it.questionId ?: it.matQuestionId ?: 0L, it.selectedOption, it.correctOption, it.isCorrect)
                }
            )
            Resource.Success(attempt)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch latest attempt")
        }
    }

    override suspend fun getStudentProfile(): Resource<StudentProfile> {
        return try {
            val dto = NetworkModule.arithmeticService.getStudentProfile()
            Resource.Success(StudentProfile(
                exists = dto.exists,
                id = dto.id,
                userId = dto.userId,
                name = dto.name,
                dateOfBirth = dto.dateOfBirth,
                gender = dto.gender,
                category = dto.category,
                residentialArea = dto.residentialArea,
                classLevel = dto.classLevel,
                stateId = dto.stateId,
                stateName = dto.stateName,
                districtId = dto.districtId,
                districtName = dto.districtName,
                preferredLanguage = dto.preferredLanguage,
                examSessionId = dto.examSessionId,
                examSessionName = dto.examSession?.sessionName
            ))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch student profile")
        }
    }

    override suspend fun createStudentProfile(
        name: String,
        dateOfBirth: String,
        gender: String,
        category: String,
        residentialArea: String,
        classLevel: Int,
        stateId: Long,
        districtId: Long,
        preferredLanguage: String,
        examSessionId: Long
    ): Resource<StudentProfile> {
        return try {
            val request = StudentProfileRequestDto(
                name = name,
                dateOfBirth = dateOfBirth,
                gender = gender,
                category = category,
                residentialArea = residentialArea,
                classLevel = classLevel,
                stateId = stateId,
                districtId = districtId,
                preferredLanguage = preferredLanguage,
                examSessionId = examSessionId
            )
            val dto = NetworkModule.arithmeticService.createStudentProfile(request)
            Resource.Success(StudentProfile(
                exists = true,
                id = dto.id,
                userId = dto.userId,
                name = dto.name,
                dateOfBirth = dto.dateOfBirth,
                gender = dto.gender,
                category = dto.category,
                residentialArea = dto.residentialArea,
                classLevel = dto.classLevel,
                stateId = dto.stateId,
                stateName = dto.stateName,
                districtId = dto.districtId,
                districtName = dto.districtName,
                preferredLanguage = dto.preferredLanguage,
                examSessionId = dto.examSessionId,
                examSessionName = dto.examSession?.sessionName
            ))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create student profile")
        }
    }

    override suspend fun getStates(): Resource<List<State>> {
        return try {
            val dtos = NetworkModule.arithmeticService.getStates()
            Resource.Success(dtos.map { State(it.id, it.code, it.name) })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch states")
        }
    }

    override suspend fun getDistricts(stateId: Long): Resource<List<District>> {
        return try {
            val dtos = NetworkModule.arithmeticService.getDistricts(stateId)
            Resource.Success(dtos.map { District(it.id, it.code, it.name) })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch districts")
        }
    }

    override suspend fun getProgress(recentPage: Int, recentLimit: Int): Resource<ProgressResponse> {
        return try {
            // 1. Ensure MAT topics are cached for name resolution
            if (cachedMatTopics.isEmpty()) {
                val matResponse = NetworkModule.arithmeticService.getMatTopics()
                cachedMatTopics = matResponse.content.map { dto ->
                    Topic(
                        id = dto.id.toString(),
                        subjectId = "mat",
                        code = dto.code,
                        nameOverride = dto.name,
                        descriptionOverride = dto.description,
                        questionCount = dto.questionCount
                    )
                }
            }

            // 2. Fetch Progress
            val dto = NetworkModule.arithmeticService.getProgress(recentPage, recentLimit)

            // 3. Map to Domain
            val response = ProgressResponse(
                overall = ProgressSummary(
                    attempts = dto.overall.attempts,
                    questions = dto.overall.questions,
                    correct = dto.overall.correct,
                    wrong = dto.overall.wrong,
                    unanswered = dto.overall.unanswered,
                    score = dto.overall.score,
                    accuracy = dto.overall.accuracy
                ),
                subjects = dto.subjects.map { s ->
                    SubjectProgress(
                        subject = s.subject,
                        attempts = s.attempts,
                        questions = s.questions,
                        correct = s.correct,
                        wrong = s.wrong,
                        unanswered = s.unanswered,
                        score = s.score,
                        accuracy = s.accuracy
                    )
                },
                topics = dto.topics.map { t ->
                    val displayName = if (t.subject == "MAT") {
                        cachedMatTopics.find { it.id == t.topicId.toString() }?.nameOverride ?: "Unknown MAT"
                    } else {
                        // Arithmetic topics. We can map them based on enum or string codes if we have a localized map.
                        // For now, let's use the code as is or look up from existing hardcoded list in repo.
                        val arithTopic = topics["arithmetic"]?.find { it.id.uppercase() == t.topic?.uppercase() }
                        arithTopic?.nameResId?.let { /* Resolving in UI? No, let's return name if possible */ }
                        t.topic ?: "Unknown"
                    }
                    TopicProgress(
                        subject = t.subject,
                        topic = t.topic,
                        topicId = t.topicId,
                        attempts = t.attempts,
                        questions = t.questions,
                        correct = t.correct,
                        wrong = t.wrong,
                        unanswered = t.unanswered,
                        score = t.score,
                        accuracy = t.accuracy,
                        displayName = displayName
                    )
                },
                recentAttempts = dto.recentAttempts.map { r ->
                    val title = when (r.subject) {
                        "MAT" -> {
                            val topicName = cachedMatTopics.find { it.id == r.topicId.toString() }?.nameOverride ?: "MAT"
                            "$topicName Set ${r.page + 1}"
                        }
                        "LANGUAGE" -> "Language Passage Set ${r.page + 1}"
                        else -> {
                            val topicName = topics["arithmetic"]?.find { it.id.uppercase() == r.topic?.uppercase() }?.id?.replace("_", " ")?.capitalize() ?: "Arithmetic"
                            "$topicName Set ${r.page + 1}"
                        }
                    }
                    RecentAttempt(
                        attemptId = r.attemptId,
                        practiceMode = r.practiceMode,
                        subject = r.subject,
                        topic = r.topic,
                        topicId = r.topicId,
                        difficulty = r.difficulty,
                        language = r.language,
                        page = r.page,
                        score = r.score,
                        questionCount = r.questionCount,
                        correctCount = r.correctCount,
                        wrongCount = r.wrongCount,
                        unansweredCount = r.unansweredCount,
                        submittedAt = r.submittedAt,
                        displayTitle = title
                    )
                }
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch progress")
        }
    }
}
