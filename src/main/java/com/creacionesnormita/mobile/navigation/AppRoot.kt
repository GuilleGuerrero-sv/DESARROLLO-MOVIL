package com.creacionesnormita.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.network.SupabaseClient
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.main.PantallaPrincipal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun AppRoot() {
    val scope = rememberCoroutineScope()
    var isAuthenticated by rememberSaveable { 
        mutableStateOf(SupabaseClient.client.auth.currentSessionOrNull() != null) 
    }

    if (isAuthenticated) {
        PantallaPrincipal(onLogout = { 
            scope.launch {
                SupabaseClient.client.auth.signOut()
                isAuthenticated = false 
            }
        })
    } else {
        PantallaAutenticacion(onAuthenticated = { isAuthenticated = true })
    }
}
