package com.jnvst.guru.data.repository

import com.jnvst.guru.data.network.SupabaseClient
import com.jnvst.guru.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepositoryImpl : AuthRepository {
    
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
