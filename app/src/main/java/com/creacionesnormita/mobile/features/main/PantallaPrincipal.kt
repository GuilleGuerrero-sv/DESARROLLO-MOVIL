package com.creacionesnormita.mobile.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.features.account.AccountContent
import com.creacionesnormita.mobile.features.appointments.AppointmentContent
import com.creacionesnormita.mobile.features.collection.CollectionContent
import com.creacionesnormita.mobile.features.home.HomeContent
import com.creacionesnormita.mobile.features.quote.QuoteContent
import com.creacionesnormita.mobile.ui.components.BrandMark
import com.creacionesnormita.mobile.ui.components.SectionDivider

private enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Outlined.Home),
    Collection("Colección", Icons.Outlined.StarBorder),
    Quote("Cotizar", Icons.AutoMirrored.Outlined.ReceiptLong),
    Appointments("Citas", Icons.Outlined.CalendarMonth),
    Account("Cuenta", Icons.Outlined.Person)
}

@Composable
fun PantallaPrincipal(
    onLogout: () -> Unit,
    onCuentaSuspendida: () -> Unit,
    onProductoClick: (Int) -> Unit,
    onAbrirGestionCatalogo: () -> Unit,
    onAbrirGestionUsuarios: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var showMenu by rememberSaveable { mutableStateOf(false) }

    val perfilController = remember { PerfilController() }

    LaunchedEffect(Unit) {
        perfilController.cargarPerfilActual()
    }

    // Si un admin bloqueó esta cuenta, se muestra la pantalla de cuenta suspendida.
    LaunchedEffect(perfilController.cuentaBloqueada) {
        if (perfilController.cuentaBloqueada) {
            onCuentaSuspendida()
        }
    }

    val perfil = perfilController.perfilActual
    val esEmpleadoOrAdmin = perfil?.esEmpleado == true

    Scaffold(
        topBar = {
            AppHeader(
                mostrarMenu = esEmpleadoOrAdmin,
                onMenuClick = { showMenu = true }
            )
        },
        bottomBar = {
            BottomNavBar(selectedTab = selectedTab, onSelected = { selectedTab = it })
        },
        containerColor = Paper
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                MainTab.Home -> HomeContent(
                    onGoCollection = { selectedTab = MainTab.Collection },
                    onGoAppointments = { selectedTab = MainTab.Appointments },
                    onGoQuote = { selectedTab = MainTab.Quote },
                    onProductoClick = onProductoClick
                )
                MainTab.Collection -> CollectionContent(
                    perfilController = perfilController,
                    onProductoClick = onProductoClick
                )
                MainTab.Quote -> QuoteContent(perfilController = perfilController)
                MainTab.Appointments -> AppointmentContent(perfilController = perfilController)
                MainTab.Account -> AccountContent(
                    perfilController = perfilController,
                    onProductoClick = onProductoClick,
                    onLogout = onLogout
                )
            }

            if (showMenu) {
                DrawerOverlay(
                    perfil = perfilController.perfilActual,
                    userEmail = perfilController.userEmail,
                    onClose = { showMenu = false },
                    onOpenGestionCatalogo = {
                        showMenu = false
                        onAbrirGestionCatalogo()
                    },
                    onOpenGestionUsuarios = {
                        showMenu = false
                        onAbrirGestionUsuarios()
                    }
                )
            }
        }
    }
}

@Composable
private fun AppHeader(mostrarMenu: Boolean, onMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (mostrarMenu) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menú", tint = Ink)
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }

            BrandMark()

            Spacer(Modifier.width(48.dp))
        }
        SectionDivider()
    }
}

@Composable
private fun BottomNavBar(selectedTab: MainTab, onSelected: (MainTab) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        MainTab.values().forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Marca,
                    selectedTextColor = Marca,
                    unselectedIconColor = Line,
                    unselectedTextColor = Line,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
