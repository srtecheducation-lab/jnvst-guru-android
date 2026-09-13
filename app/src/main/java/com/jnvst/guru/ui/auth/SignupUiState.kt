package com.jnvst.guru.ui.auth

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignupSuccessful: Boolean = false
)
