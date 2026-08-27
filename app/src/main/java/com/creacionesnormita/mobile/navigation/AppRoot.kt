package com.creacionesnormita.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.network.SupabaseClient
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.detail.PantallaDetalleProducto
import com.creacionesnormita.mobile.features.main.PantallaPrincipal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

private const val RUTA_AUTH = "auth"
private const val RUTA_PRINCIPAL = "principal"
private const val RUTA_DETALLE = "detalle/{productoId}"

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RUTA_AUTH) {
        composable(RUTA_AUTH) {
            PantallaAutenticacion(
                onAuthenticated = {
                    navController.navigate(RUTA_PRINCIPAL) {
                        popUpTo(RUTA_AUTH) { inclusive = true }
                    }
                }
            )
        }
        composable(RUTA_PRINCIPAL) {
            PantallaPrincipal(
                onLogout = {
                    navController.navigate(RUTA_AUTH) {
                        popUpTo(RUTA_PRINCIPAL) { inclusive = true }
                    }
                },
                onProductoClick = { productoId ->
                    navController.navigate("detalle/$productoId")
                }
            )
        }
        composable(
            route = RUTA_DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: return@composable
            PantallaDetalleProducto(
                productoId = productoId,
                onBack = { navController.popBackStack() }
            )
        }
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