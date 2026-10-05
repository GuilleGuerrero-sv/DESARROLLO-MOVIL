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
import com.creacionesnormita.mobile.MainActivity
import com.creacionesnormita.mobile.core.network.SupabaseClient
import com.creacionesnormita.mobile.features.admin.PantallaEditarProducto
import com.creacionesnormita.mobile.features.admin.PantallaGestionCatalogo
import com.creacionesnormita.mobile.features.admin.PantallaGestionUsuarios
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.auth.PantallaCuentaSuspendida
import com.creacionesnormita.mobile.features.detail.PantallaDetalleProducto
import com.creacionesnormita.mobile.features.main.PantallaPrincipal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

private const val RUTA_AUTH = "auth"
private const val RUTA_PRINCIPAL = "principal"
private const val RUTA_DETALLE = "detalle/{productoId}"
private const val RUTA_GESTION_CATALOGO = "gestion-catalogo"
private const val RUTA_NUEVO_PRODUCTO = "gestion-catalogo/nuevo"
private const val RUTA_EDITAR_PRODUCTO = "gestion-catalogo/editar/{productoId}"
private const val RUTA_GESTION_USUARIOS = "gestion-usuarios"
private const val RUTA_SUSPENDIDA = "cuenta-suspendida"

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var isInitializing by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf(RUTA_AUTH) }

    // 1. Solo decide con QUÉ pantalla arrancar (sin navegar todavía, el NavHost ni existe aún).
    LaunchedEffect(Unit) {
        if (SupabaseClient.isConfigured()) {
            try {
                SupabaseClient.client.auth.awaitInitialization()
                val statusInicial = SupabaseClient.client.auth.sessionStatus.value
                val esRecuperacion = MainActivity.esFlujoRecuperacion
                val esRegistro = MainActivity.esFlujoRegistro
                startDestination = if (statusInicial is SessionStatus.Authenticated && !esRecuperacion && !esRegistro) RUTA_PRINCIPAL else RUTA_AUTH
            } catch (e: Exception) {
                e.printStackTrace()
                startDestination = RUTA_AUTH
            }
        } else {
            startDestination = RUTA_AUTH
        }
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
                onCuentaSuspendida = {
                    scope.launch {
                        // Primero navega a la pantalla de suspensión y luego cierra la sesión
                        navController.navigate(RUTA_SUSPENDIDA) {
                            popUpTo(0) { inclusive = true }
                        }
                        SupabaseClient.client.auth.signOut()
                    }
                },
                onProductoClick = { productoId ->
                    navController.navigate("detalle/$productoId")
                },
                onAbrirGestionCatalogo = {
                    navController.navigate(RUTA_GESTION_CATALOGO)
                },
                onAbrirGestionUsuarios = {
                    navController.navigate(RUTA_GESTION_USUARIOS)
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
        composable(RUTA_GESTION_CATALOGO) {
            PantallaGestionCatalogo(
                onBack = { navController.popBackStack() },
                onNuevoProducto = { navController.navigate(RUTA_NUEVO_PRODUCTO) },
                onEditarProducto = { productoId -> navController.navigate("gestion-catalogo/editar/$productoId") }
            )
        }
        composable(RUTA_NUEVO_PRODUCTO) {
            PantallaEditarProducto(productoId = null, onBack = { navController.popBackStack() })
        }
        composable(
            route = RUTA_EDITAR_PRODUCTO,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: return@composable
            PantallaEditarProducto(productoId = productoId, onBack = { navController.popBackStack() })
        }
        composable(RUTA_GESTION_USUARIOS) {
            PantallaGestionUsuarios(onBack = { navController.popBackStack() })
        }
        composable(RUTA_SUSPENDIDA) {
            PantallaCuentaSuspendida(
                onVolverAlLogin = {
                    navController.navigate(RUTA_AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }

    // 2. Ya con el NavHost montado, ahora sí escucha cambios de sesión EN VIVO
    // (login exitoso, logout, expiración) y navega en consecuencia.
    LaunchedEffect(navController) {
        if (SupabaseClient.isConfigured()) {
            SupabaseClient.client.auth.sessionStatus.collect { status ->
                // Si se está mostrando la pantalla de cuenta suspendida, no la reemplaces
                if (navController.currentDestination?.route == RUTA_SUSPENDIDA) return@collect
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val esRecuperacion = MainActivity.esFlujoRecuperacion
                        val esRegistro = MainActivity.esFlujoRegistro
                        if (!esRecuperacion && !esRegistro && navController.currentDestination?.route != RUTA_PRINCIPAL) {
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
}