package com.jnvst.guru.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionStatus: Flow<Boolean>
    fun isLoggedIn(): Boolean
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun logout()
}
