package com.creacionesnormita.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.creacionesnormita.mobile.core.design.CreacionesTheme
import com.creacionesnormita.mobile.core.network.SupabaseClient
import com.creacionesnormita.mobile.navigation.AppRoot
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    companion object {
        var esFlujoRecuperacion by mutableStateOf(false)
        var esFlujoRegistro by mutableStateOf(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLinkIntent(intent)

        setContent {
            CreacionesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        if (intent == null || !SupabaseClient.isConfigured()) return
        val data = intent.data
        if (data != null) {
            val uriString = data.toString()
            if (data.host == "reset-password" || uriString.contains("type=recovery") || uriString.contains("reset")) {
                esFlujoRecuperacion = true
            }
            try {
                SupabaseClient.client.handleDeeplinks(
                    intent = intent,
                    onSessionSuccess = {
                        esFlujoRecuperacion = true
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
