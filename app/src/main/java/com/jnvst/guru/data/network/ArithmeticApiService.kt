package com.jnvst.guru.data.network

import com.jnvst.guru.data.network.dto.ArithmeticQuestionDto
import com.jnvst.guru.data.network.dto.MatQuestionDto
import com.jnvst.guru.data.network.dto.MatTopicDto
import com.jnvst.guru.data.network.dto.PageResponse
import com.jnvst.guru.data.network.dto.PracticeAttemptRequestDto
import com.jnvst.guru.data.network.dto.PracticeAttemptResponseDto
import com.jnvst.guru.data.network.dto.PracticeStatusResponseDto
import com.jnvst.guru.data.network.dto.StudentProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ArithmeticApiService {
    
    @GET("/api/v1/student/arithmetic-questions")
    suspend fun getArithmeticQuestions(
        @Query("questionType") questionType: String?,
        @Query("difficulty") difficulty: String?,
        @Query("language") language: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<ArithmeticQuestionDto>

    @GET("/api/v1/student/mat-topics")
    suspend fun getMatTopics(): PageResponse<MatTopicDto>

    @GET("/api/v1/student/mat-questions")
    suspend fun getMatQuestions(
        @Query("topicId") topicId: Long?,
        @Query("difficulty") difficulty: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<MatQuestionDto>

    @POST("/api/v1/student/practice-attempts")
    suspend fun submitPracticeAttempt(
        @Body request: PracticeAttemptRequestDto
    ): PracticeAttemptResponseDto

    @GET("/api/v1/student/practice-attempts")
    suspend fun getPracticeStatus(
        @Query("practiceMode") practiceMode: String,
        @Query("subject") subject: String,
        @Query("topic") topic: String?,
        @Query("topicId") topicId: Long?,
        @Query("difficulty") difficulty: String
    ): PracticeStatusResponseDto

    @GET("/api/v1/student/practice-attempts/latest")
    suspend fun getLatestAttempt(
        @Query("practiceMode") practiceMode: String,
        @Query("subject") subject: String,
        @Query("topic") topic: String?,
        @Query("topicId") topicId: Long?,
        @Query("difficulty") difficulty: String,
        @Query("page") page: Int
    ): PracticeAttemptResponseDto

    @GET("/api/v1/student-profiles/me")
    suspend fun getStudentProfile(): StudentProfileDto
}
