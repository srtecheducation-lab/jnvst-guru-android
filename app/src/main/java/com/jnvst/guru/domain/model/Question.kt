package com.jnvst.guru.domain.model

data class Question(
    val id: Long,
    val questionType: String,
    val questionText: String,
    val options: List<String>,
    val difficulty: String,
    var selectedOptionIndex: Int = -1,
    var correctOptionIndex: Int = -1
)
