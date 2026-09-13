package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentProfileDto(
    val exists: Boolean = false,
    val id: Long? = null,
    val userId: Long? = null,
    val name: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val category: String? = null,
    val residentialArea: String? = null,
    val classLevel: Int? = null,
    val stateId: Long? = null,
    val stateName: String? = null,
    val districtId: Long? = null,
    val districtName: String? = null,
    val preferredLanguage: String? = null,
    val examSessionId: Long? = null,
    val examSession: ExamSessionDto? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ExamSessionDto(
    val id: Long,
    val sessionName: String
)
