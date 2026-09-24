package com.jnvst.guru.data.local

import android.content.Context
import android.content.SharedPreferences
import com.jnvst.guru.domain.model.StudentProfile
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class CachedProfileDto(
    val exists: Boolean,
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
    val examSessionName: String? = null
)

class StudentProfileLocalDataSource(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_profile_cache", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun getProfile(userId: String): StudentProfile? {
        if (userId.isBlank()) return null
        val jsonString = prefs.getString("profile_$userId", null) ?: return null
        return try {
            val dto = json.decodeFromString<CachedProfileDto>(jsonString)
            StudentProfile(
                exists = dto.exists,
                id = dto.id,
                userId = dto.userId,
                name = dto.name,
                dateOfBirth = dto.dateOfBirth,
                gender = dto.gender,
                category = dto.category,
                residentialArea = dto.residentialArea,
                classLevel = dto.classLevel,
                stateId = dto.stateId,
                stateName = dto.stateName,
                districtId = dto.districtId,
                districtName = dto.districtName,
                preferredLanguage = dto.preferredLanguage,
                examSessionId = dto.examSessionId,
                examSessionName = dto.examSessionName
            )
        } catch (_: Exception) {
            null
        }
    }

    fun saveProfile(userId: String, profile: StudentProfile) {
        if (userId.isBlank()) return
        val dto = CachedProfileDto(
            exists = profile.exists,
            id = profile.id,
            userId = profile.userId,
            name = profile.name,
            dateOfBirth = profile.dateOfBirth,
            gender = profile.gender,
            category = profile.category,
            residentialArea = profile.residentialArea,
            classLevel = profile.classLevel,
            stateId = profile.stateId,
            stateName = profile.stateName,
            districtId = profile.districtId,
            districtName = profile.districtName,
            preferredLanguage = profile.preferredLanguage,
            examSessionId = profile.examSessionId,
            examSessionName = profile.examSessionName
        )
        val jsonString = json.encodeToString(dto)
        prefs.edit().putString("profile_$userId", jsonString).apply()
    }

    fun clearProfile(userId: String) {
        if (userId.isNotBlank()) {
            prefs.edit().remove("profile_$userId").apply()
        }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
