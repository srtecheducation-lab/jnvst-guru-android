package com.jnvst.guru.domain.repository

import com.jnvst.guru.domain.model.*
import com.jnvst.guru.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun getSubjects(): Flow<List<Subject>>
    fun getTopicsForSubject(subjectId: String): Flow<List<Topic>>
    fun getSubjectById(subjectId: String): Flow<Subject?>
    fun getMockTests(): Flow<List<Test>>
    fun getTestById(testId: String): Flow<Test?>
    
    suspend fun getArithmeticQuestions(
        type: String?,
        difficulty: String?,
        language: String?,
        page: Int,
        size: Int
    ): Resource<List<Question>>

    suspend fun getMatTopics(): Resource<List<Topic>>

    suspend fun getMatQuestions(
        topicId: Long?,
        difficulty: String?,
        page: Int,
        size: Int
    ): Resource<List<Question>>

    suspend fun getLanguageQuestions(
        language: String,
        page: Int,
        size: Int
    ): Resource<List<LanguagePassage>>

    suspend fun submitPracticeAttempt(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?,
        page: Int,
        answers: List<Pair<Long, Int?>> // questionId to selectedOptionIndex
    ): Resource<PracticeResult>

    suspend fun getPracticeStatus(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?
    ): Resource<List<SetStatus>>

    suspend fun getLatestAttempt(
        mode: String,
        subject: String,
        topic: String?,
        topicId: Long?,
        difficulty: String,
        language: String?,
        page: Int
    ): Resource<PracticeAttempt>

    suspend fun getStudentProfile(): Resource<StudentProfile>

    suspend fun createStudentProfile(
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
    ): Resource<StudentProfile>

    suspend fun getStates(): Resource<List<State>>

    suspend fun getDistricts(stateId: Long): Resource<List<District>>

    suspend fun getProgress(recentPage: Int, recentLimit: Int): Resource<ProgressResponse>
}
