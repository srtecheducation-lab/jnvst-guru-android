package com.jnvst.guru.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnvst.guru.data.repository.AuthRepositoryImpl
import com.jnvst.guru.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { isAuthenticated ->
                _uiState.update { it.copy(
                    isInitializing = false,
                    isLoginSuccessful = isAuthenticated
                ) }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(email, password)
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Login failed") }
            }
        }
    }

    fun loginWithIdToken(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.loginWithIdToken(idToken)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Google Sign-In failed") }
            }
        }
    }
    
    fun checkSession(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
