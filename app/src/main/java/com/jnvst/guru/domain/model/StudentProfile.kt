package com.jnvst.guru.domain.model

data class StudentProfile(
    val exists: Boolean,
    val id: Long? = null,
    val userId: Long? = null,
    val name: String?,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val category: String? = null,
    val residentialArea: String? = null,
    val classLevel: Int? = null,
    val stateId: Long? = null,
    val stateName: String? = null,
    val districtId: Long? = null,
    val districtName: String? = null,
    val preferredLanguage: String?,
    val examSessionId: Long? = null,
    val examSessionName: String? = null
)
