package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentProfileRequestDto(
    val name: String,
    val dateOfBirth: String,
    val gender: String,
    val category: String,
    val residentialArea: String,
    val classLevel: Int,
    val stateId: Long,
    val districtId: Long,
    val preferredLanguage: String,
    val examSessionId: Long
)
