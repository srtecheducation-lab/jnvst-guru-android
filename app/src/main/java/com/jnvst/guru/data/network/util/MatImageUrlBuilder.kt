package com.jnvst.guru.data.network.util

object MatImageUrlBuilder {
    private const val BASE_URL = "https://eolupxlsxwpmaqfyyeqo.supabase.co/storage/v1/object/public/mat-images/"

    fun buildUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else "$BASE_URL$path"
    }
}
