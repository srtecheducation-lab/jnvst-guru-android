package com.jnvst.guru.domain.model

data class Topic(
    val id: String,
    val subjectId: String,
    val nameResId: Int,
    val descriptionResId: Int? = null,
    val questionCount: Int = 0,
    val progress: Int = 0, // 0-100
    val durationMinutes: Int = 0,
    val difficultyResId: Int? = null
)
