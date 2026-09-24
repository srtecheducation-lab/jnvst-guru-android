package com.jnvst.guru.ui.profile

import com.jnvst.guru.domain.model.StudentProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: StudentProfile? = null,
    val email: String? = null,
    val error: String? = null
)
