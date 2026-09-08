package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArithmeticQuestionDto(
    val id: Long,
    val questionType: String,
    val questionText: String? = null,
    val optionA: String? = null,
    val optionB: String? = null,
    val optionC: String? = null,
    val optionD: String? = null,
    val difficulty: String
)
