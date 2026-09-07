package com.jnvst.guru.data.network

import com.jnvst.guru.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
    }

    /**
     * Retrieves the current authenticated session's access token.
     * Returns null if no user is authenticated.
     */
    fun getAccessToken(): String? {
        return client.auth.currentSessionOrNull()?.accessToken
    }
}
