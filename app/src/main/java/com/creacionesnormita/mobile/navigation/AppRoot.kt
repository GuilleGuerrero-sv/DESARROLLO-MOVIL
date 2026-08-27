package com.creacionesnormita.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.detail.PantallaDetalleProducto
import com.creacionesnormita.mobile.features.main.PantallaPrincipal

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
    }
}