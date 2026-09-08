package com.jnvst.guru.data.repository

import com.jnvst.guru.R
import com.jnvst.guru.data.network.NetworkModule
import com.jnvst.guru.data.network.dto.AnswerRequestDto
import com.jnvst.guru.data.network.dto.PracticeAttemptRequestDto
import com.jnvst.guru.domain.model.*
import com.jnvst.guru.domain.repository.PracticeRepository
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PracticeRepositoryImpl : PracticeRepository {

    private val subjects = listOf(
        // ... (existing subjects)
        Subject(
            id = "mental_ability",
            nameResId = R.string.subject_mental_ability,
            descriptionResId = R.string.subject_mental_desc,
            iconResId = R.drawable.ic_launcher_foreground,
            topicCount = 12,
            colorHex = "#2196F3"
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
            Topic("analogy", "mental_ability", R.string.topic_analogy, questionCount = 20, progress = 85, durationMinutes = 30, difficultyResId = R.string.difficulty_medium),
            Topic("classification", "mental_ability", R.string.topic_classification, questionCount = 25, progress = 72, durationMinutes = 35, difficultyResId = R.string.difficulty_hard),
            Topic("figure_series", "mental_ability", R.string.topic_figure_series, questionCount = 30, progress = 54, durationMinutes = 40, difficultyResId = R.string.difficulty_easy),
            Topic("direction_sense", "mental_ability", R.string.topic_grammar, questionCount = 15, progress = 91, durationMinutes = 20, difficultyResId = R.string.difficulty_medium), // Reusing grammar string for placeholder topic
            Topic("mirror_images", "mental_ability", R.string.topic_grammar, questionCount = 20, progress = 68, durationMinutes = 25, difficultyResId = R.string.difficulty_hard)
        ),
        "arithmetic" to listOf(
            Topic("number_system", "arithmetic", R.string.topic_number_system, questionCount = 40, progress = 45, durationMinutes = 60, difficultyResId = R.string.difficulty_hard),
            Topic("fractions", "arithmetic", R.string.topic_fractions, questionCount = 30, progress = 60, durationMinutes = 45, difficultyResId = R.string.difficulty_medium),
            Topic("decimals", "arithmetic", R.string.topic_decimals, questionCount = 25, progress = 75, durationMinutes = 30, difficultyResId = R.string.difficulty_easy)
        ),
        "language" to listOf(
            Topic("reading_comp", "language", R.string.topic_reading_comprehension, questionCount = 10, progress = 30, durationMinutes = 20, difficultyResId = R.string.difficulty_medium),
            Topic("vocabulary", "language", R.string.topic_vocabulary, questionCount = 50, progress = 20, durationMinutes = 40, difficultyResId = R.string.difficulty_easy),
            Topic("grammar", "language", R.string.topic_grammar, questionCount = 100, progress = 10, durationMinutes = 90, difficultyResId = R.string.difficulty_hard)
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

    override fun getTopicsForSubject(subjectId: String): Flow<List<Topic>> = 
        flowOf(topics[subjectId] ?: emptyList())

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

    override suspend fun submitPracticeAttempt(
        mode: String,
        subject: String,
        topic: String?,
        difficulty: String,
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
                practiceMode = mode.uppercase(),
                subject = subject.uppercase(),
                topic = topic?.uppercase(),
                difficulty = difficulty.uppercase(),
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
        difficulty: String
    ): Resource<List<SetStatus>> {
        return try {
            val response = NetworkModule.arithmeticService.getPracticeStatus(
                practiceMode = mode.uppercase(),
                subject = subject.uppercase(),
                topic = topic?.uppercase(),
                difficulty = difficulty.uppercase()
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
        difficulty: String,
        page: Int
    ): Resource<PracticeAttempt> {
        return try {
            val response = NetworkModule.arithmeticService.getLatestAttempt(
                practiceMode = mode.uppercase(),
                subject = subject.uppercase(),
                topic = topic?.uppercase(),
                difficulty = difficulty.uppercase(),
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
                    PracticeAnswer(it.questionId, it.selectedOption, it.correctOption, it.isCorrect)
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
                name = dto.name,
                preferredLanguage = dto.preferredLanguage
            ))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch student profile")
        }
    }
}
