package com.jnvst.guru.domain.model

sealed class PracticeSessionItem {
    data class PassageItem(
        val id: Long,
        val number: Int,
        val text: String,
        val totalPassages: Int = 4
    ) : PracticeSessionItem()

    data class QuestionItem(
        val question: Question,
        val passageId: Long? = null,
        val passageNumber: Int? = null
    ) : PracticeSessionItem()
}
