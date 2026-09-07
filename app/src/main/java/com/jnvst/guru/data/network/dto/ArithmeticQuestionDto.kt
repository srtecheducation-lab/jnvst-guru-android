package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArithmeticQuestionDto(
    val id: Long,
    val questionType: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val difficulty: String
)
