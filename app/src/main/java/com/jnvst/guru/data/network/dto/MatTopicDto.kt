package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatTopicDto(
    val id: Long,
    val code: String? = null,
    val name: String,
    val description: String? = null,
    val sortOrder: Int = 0,
    val questionCount: Int = 0
)
