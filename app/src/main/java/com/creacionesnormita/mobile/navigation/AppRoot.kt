package com.creacionesnormita.mobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    val scope = rememberCoroutineScope()

    var isInitializing by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf(RUTA_AUTH) }

    // 1. Solo decide con QUÉ pantalla arrancar (sin navegar todavía, el NavHost ni existe aún).
    LaunchedEffect(Unit) {
        SupabaseClient.client.auth.awaitInitialization()
        val statusInicial = SupabaseClient.client.auth.sessionStatus.value
        startDestination = if (statusInicial is SessionStatus.Authenticated) RUTA_PRINCIPAL else RUTA_AUTH
        isInitializing = false
    }

    if (isInitializing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(RUTA_AUTH) {
            PantallaAutenticacion(
                onAuthenticated = {
                    // La navegación real ocurre en el LaunchedEffect de abajo,
                    // apenas Supabase confirme el nuevo sessionStatus.
                }
            )
        }
        composable(RUTA_PRINCIPAL) {
            PantallaPrincipal(
                onLogout = {
                    scope.launch {
                        SupabaseClient.client.auth.signOut()
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
    }

    // 2. Ya con el NavHost montado, ahora sí escucha cambios de sesión EN VIVO
    // (login exitoso, logout, expiración) y navega en consecuencia.
    LaunchedEffect(navController) {
        SupabaseClient.client.auth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    if (navController.currentDestination?.route != RUTA_PRINCIPAL) {
                        navController.navigate(RUTA_PRINCIPAL) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
                else -> {
                    if (navController.currentDestination?.route != RUTA_AUTH) {
                        navController.navigate(RUTA_AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}