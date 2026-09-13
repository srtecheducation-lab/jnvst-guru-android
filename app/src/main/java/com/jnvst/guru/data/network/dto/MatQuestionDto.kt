package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatQuestionDto(
    val id: Long,
    val topicId: Long,
    val questionImageUrl: String? = null,
    val optionAImageUrl: String,
    val optionBImageUrl: String,
    val optionCImageUrl: String,
    val optionDImageUrl: String,
    val difficulty: String
)
