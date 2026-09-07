package com.jnvst.guru.domain.model

data class PracticeResult(
    val score: Int,
    val questionCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int
)
