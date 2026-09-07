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
        page: Int,
        size: Int
    ): Resource<List<Question>>

    suspend fun submitPracticeAttempt(
        mode: String,
        subject: String,
        topic: String?,
        difficulty: String,
        page: Int,
        answers: List<Pair<Long, Int?>> // questionId to selectedOptionIndex
    ): Resource<PracticeResult>

    suspend fun getPracticeStatus(
        mode: String,
        subject: String,
        topic: String?,
        difficulty: String
    ): Resource<List<SetStatus>>

    suspend fun getLatestAttempt(
        mode: String,
        subject: String,
        topic: String?,
        difficulty: String,
        page: Int
    ): Resource<PracticeAttempt>
}
