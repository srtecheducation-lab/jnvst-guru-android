package com.jnvst.guru.ui.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isInitializing: Boolean = true,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)
