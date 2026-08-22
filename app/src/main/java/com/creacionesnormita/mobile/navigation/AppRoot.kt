package com.creacionesnormita.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.features.auth.PantallaAutenticacion
import com.creacionesnormita.mobile.features.main.PantallaPrincipal

@Composable
fun AppRoot() {
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }

    if (isAuthenticated) {
        PantallaPrincipal(onLogout = { isAuthenticated = false })
    } else {
        PantallaAutenticacion(onAuthenticated = { isAuthenticated = true })
    }
}
