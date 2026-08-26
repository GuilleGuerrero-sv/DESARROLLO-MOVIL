package com.creacionesnormita.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.network.SupabaseClient
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.main.PantallaPrincipal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

@Composable
fun AppRoot() {
    val scope = rememberCoroutineScope()
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var isInitializing by rememberSaveable { mutableStateOf(true) }

    // Observar el estado de la sesión de Supabase de forma reactiva
    LaunchedEffect(Unit) {
        // Esperar a que Supabase cargue la sesión guardada del disco
        SupabaseClient.client.auth.awaitInitialization()
        
        SupabaseClient.client.auth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    isAuthenticated = true
                    isInitializing = false
                }
                else -> {
                    isAuthenticated = false
                    isInitializing = false
                }
            }
        }
    }

    if (isInitializing) {
        // Podrías poner un indicador de carga aquí
        return
    }

    if (isAuthenticated) {
        PantallaPrincipal(onLogout = { 
            scope.launch {
                SupabaseClient.client.auth.signOut()
            }
        })
    } else {
        PantallaAutenticacion(onAuthenticated = { 
            // El estado cambiará mediante el flow collect de arriba
        })
    }
}
