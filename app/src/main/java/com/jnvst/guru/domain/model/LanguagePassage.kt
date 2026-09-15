package com.jnvst.guru.domain.model

data class LanguagePassage(
    val id: Long,
    val number: Int,
    val text: String,
    val questions: List<Question>
)
