package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentLanguagePassageResponseDto(
    val passageId: Long,
    val passageNumber: Int,
    val passageText: String? = null,
    val questions: List<LanguageQuestionDto> = emptyList()
)

@Serializable
data class LanguageQuestionDto(
    val questionId: Long,
    val questionNumber: Int,
    val questionText: String? = null,
    val optionA: String? = null,
    val optionB: String? = null,
    val optionC: String? = null,
    val optionD: String? = null
)
