package com.jnvst.guru.domain.model

data class Test(
    val id: String,
    val name: String,
    val questionCount: Int,
    val durationMinutes: Int,
    val isLocked: Boolean = false,
    val className: String = "Class 6"
)
