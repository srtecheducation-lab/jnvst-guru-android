package com.jnvst.guru.domain.model

data class Subject(
    val id: String,
    val nameResId: Int,
    val descriptionResId: Int? = null,
    val iconResId: Int? = null,
    val topicCount: Int = 0,
    val colorHex: String? = null
)
