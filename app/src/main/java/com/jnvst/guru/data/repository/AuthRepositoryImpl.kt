package com.jnvst.guru.data.repository

import com.jnvst.guru.data.network.SupabaseClient
import com.jnvst.guru.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl : AuthRepository {
    
    override val sessionStatus: Flow<Boolean> = SupabaseClient.client.auth.sessionStatus.map {
        it is SessionStatus.Authenticated
    }

    override fun isLoggedIn(): Boolean {
        return SupabaseClient.client.auth.currentSessionOrNull() != null
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            SupabaseClient.client.auth.signOut()
        } catch (_: Exception) {
        }
    }
}
