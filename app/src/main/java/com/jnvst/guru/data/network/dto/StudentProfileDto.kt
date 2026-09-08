package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentProfileDto(
    val exists: Boolean = false,
    val id: Long? = null,
    val userId: Long? = null,
    val name: String? = null,
    val preferredLanguage: String? = null
)
