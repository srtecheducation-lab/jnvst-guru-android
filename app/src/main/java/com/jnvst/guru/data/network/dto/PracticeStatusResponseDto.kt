package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PracticeStatusResponseDto(
    val practiceMode: String,
    val subject: String,
    val topic: String? = null,
    val topicId: Long? = null,
    val difficulty: String? = null,
    val sets: List<SetStatusDto>
)

@Serializable
data class SetStatusDto(
    val page: Int,
    val setNumber: Int,
    val questionCount: Int,
    val completed: Boolean
)
