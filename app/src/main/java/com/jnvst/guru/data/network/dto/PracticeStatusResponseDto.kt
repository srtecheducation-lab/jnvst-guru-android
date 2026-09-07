package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PracticeStatusResponseDto(
    val practiceMode: String,
    val subject: String,
    val topic: String?,
    val difficulty: String,
    val sets: List<SetStatusDto>
)

@Serializable
data class SetStatusDto(
    val page: Int,
    val setNumber: Int,
    val questionCount: Int,
    val completed: Boolean
)
