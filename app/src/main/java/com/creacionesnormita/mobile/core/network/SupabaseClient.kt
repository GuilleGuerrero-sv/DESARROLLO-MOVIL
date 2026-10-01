package com.creacionesnormita.mobile.core.network

import com.creacionesnormita.mobile.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client: SupabaseClient by lazy {
        val url = BuildConfig.SUPABASE_URL.takeIf { isConfigured() } ?: "https://placeholder.supabase.co"
        val key = BuildConfig.SUPABASE_KEY.takeIf { isConfigured() } ?: "placeholder-key"

        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth) {
                scheme = "creacionesnormita"
                host = "reset-password"
            }
            install(Postgrest)
        }
    }

    fun isConfigured(): Boolean {
        val url = BuildConfig.SUPABASE_URL
        val key = BuildConfig.SUPABASE_KEY

        val isPlaceholderUrl = url.contains("tu-proyecto", ignoreCase = true) ||
                url.contains("placeholder", ignoreCase = true) ||
                url.contains("example", ignoreCase = true)

        val isPlaceholderKey = key.contains("tu-anon-key", ignoreCase = true) ||
                key.contains("placeholder", ignoreCase = true) ||
                key.contains("your-key", ignoreCase = true)

        return url.isNotBlank() && url != "null" && url.startsWith("http") && !isPlaceholderUrl &&
                key.isNotBlank() && key != "null" && !isPlaceholderKey
    }
}