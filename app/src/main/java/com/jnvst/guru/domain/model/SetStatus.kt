package com.jnvst.guru.domain.model

data class SetStatus(
    val page: Int,
    val setNumber: Int,
    val questionCount: Int,
    val completed: Boolean,
    val score: Int? = null // Handled locally if needed or just optional
)
