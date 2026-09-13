package com.jnvst.guru.domain.model

data class Question(
    val id: Long,
    val questionType: String,
    val questionText: String = "",
    val questionImageUrl: String? = null,
    val options: List<String> = emptyList(),
    val optionImageUrls: List<String>? = null,
    val difficulty: String,
    var selectedOptionIndex: Int = -1,
    var correctOptionIndex: Int = -1
)
