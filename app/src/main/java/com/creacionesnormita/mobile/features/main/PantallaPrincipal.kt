package com.creacionesnormita.mobile.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Gold
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.Sage
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.sample.serviciosDestacados
import com.creacionesnormita.mobile.core.sample.vestidos
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AutoCarousel
import com.creacionesnormita.mobile.ui.components.BrandMark
import com.creacionesnormita.mobile.ui.components.PlaceholderLines
import com.creacionesnormita.mobile.ui.components.SectionDivider
import com.creacionesnormita.mobile.ui.components.StatPill
import com.creacionesnormita.mobile.ui.components.WireImage

private enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Outlined.Home),
    Collection("Colección", Icons.Outlined.StarBorder),
    Quote("Cotizar", Icons.AutoMirrored.Outlined.ReceiptLong),
    Appointments("Citas", Icons.Outlined.CalendarMonth),
    Account("Cuenta", Icons.Outlined.Person)
}

@Composable
fun PantallaPrincipal(onLogout: () -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var showMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { AppHeader(onMenuClick = { showMenu = true }) },
        bottomBar = {
            BottomNavBar(selectedTab = selectedTab, onSelected = { selectedTab = it })
        },
        containerColor = Paper
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                MainTab.Home -> HomeContent(onGoCollection = { selectedTab = MainTab.Collection }, onGoAppointments = { selectedTab = MainTab.Appointments })
                MainTab.Collection -> CollectionContent()
                MainTab.Quote -> QuoteContent()
                MainTab.Appointments -> AppointmentContent()
                MainTab.Account -> AccountContent(onLogout = onLogout)
            }

            if (showMenu) {
                DrawerOverlay(onClose = { showMenu = false })
            }
        }
    }
}

@Composable
private fun AppHeader(onMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menú", tint = Ink)
            }
            BrandMark()
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más", tint = Ink)
            }
        }
        SectionDivider()
    }
}

@Composable
private fun HomeContent(onGoCollection: () -> Unit, onGoAppointments: () -> Unit) {
    // Controller (MVC): maneja la carga del vestido del día desde Supabase.
    val productoController = remember { ProductoController() }

    LaunchedEffect(Unit) {
        productoController.cargarVestidoDelDia()
    }

    // Esta primera pantalla sigue el mockup: bienvenida, vestido destacado y accesos rápidos.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            PlaceholderLines(widths = listOf(.32f, .86f, .62f, .78f))
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(text = "Ver colección", onClick = onGoCollection, modifier = Modifier.weight(1f))
                ActionButton(text = "WhatsApp", onClick = {}, modifier = Modifier.weight(1f), filled = false)
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(.98f)
                    .clip(RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp, bottomStart = 22.dp, bottomEnd = 22.dp))
                    .border(1.dp, Ink, RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp, bottomStart = 22.dp, bottomEnd = 22.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val vestido = productoController.vestidoDelDia
                when {
                    productoController.cargando -> {
                        Text("Cargando vestido del día...", color = SoftInk, fontSize = 12.sp)
                    }
                    productoController.error != null -> {
                        Text("No se pudo cargar: ${productoController.error}", color = SoftInk, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                    }
                    vestido != null -> {
                        AutoCarousel(
                            imagenes = vestido.imagenes,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp, bottomStart = 22.dp, bottomEnd = 22.dp))
                        )
                    }
                    else -> {
                        Text("Aún no hay un vestido del día configurado", color = SoftInk, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                    }
                }
            }
        }
        productoController.vestidoDelDia?.let { vestido ->
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(vestido.nombre, color = SoftInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("$${vestido.precio}", color = Marca, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                serviciosDestacados.forEachIndexed { index, item ->
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Outlined.StarBorder
                                1 -> Icons.Outlined.Home
                                else -> Icons.AutoMirrored.Outlined.Send
                            },
                            contentDescription = null,
                            tint = Ink
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 9.dp)
                                .fillMaxWidth(.78f)
                                .height(7.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Line)
                        )
                        Text(item.descripcion, color = SoftInk, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Ink, RoundedCornerShape(18.dp))
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Ink)
                PlaceholderLines(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth(),
                    widths = listOf(.62f, .82f)
                )
                ActionButton(text = "Agendar cita en videollamada", onClick = onGoAppointments, modifier = Modifier.padding(top = 16.dp).fillMaxWidth(.72f))
            }
        }
        item {
            WireImage(
                label = "Video de muestra",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            )
        }
    }
}

@Composable
private fun CollectionContent() {
    Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatPill("Todos · ${vestidos.size}")
            StatPill("Disponible ahora")
            StatPill("A tu medida")
        }
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vestidos) { vestido ->
                DressCard(name = vestido.nombre, status = vestido.estado)
            }
        }
    }
}

@Composable
private fun DressCard(name: String, status: String) {
    Column {
        WireImage(label = name, modifier = Modifier.fillMaxWidth().aspectRatio(.82f))
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(.82f)
                .height(7.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Line)
        )
        StatPill(text = status, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun QuoteContent() {
    // La cotización todavía usa datos de muestra; luego se conectará con productos reales.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(2) { index ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WireImage("Vestido", modifier = Modifier.size(74.dp))
                PlaceholderLines(modifier = Modifier.weight(1f), widths = listOf(.72f, .46f))
                StatPill("- ${index + 1} +")
                Icon(Icons.Outlined.Close, contentDescription = null, tint = SoftInk, modifier = Modifier.size(18.dp))
            }
        }
        item { QuoteField("Nombre") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuoteField("WhatsApp", modifier = Modifier.weight(1f))
                QuoteField("Email", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatPill("Nacional", modifier = Modifier.weight(1f))
                StatPill("Internacional", modifier = Modifier.weight(1f))
            }
        }
        item { QuoteField("Fecha del evento + mín. según destino") }
        item { QuoteField("Notas", minHeight = 76.dp) }
        item { ActionButton(text = "Enviar cotización", onClick = {}, modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun QuoteField(label: String, modifier: Modifier = Modifier, minHeight: androidx.compose.ui.unit.Dp = 54.dp) {
    Column(
        modifier = modifier
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .height(minHeight)
    ) {
        Text(label.uppercase(), color = SoftInk, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(.7f)
                .height(7.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Line)
        )
    }
}

@Composable
private fun AppointmentContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Ink, modifier = Modifier.size(30.dp))
            PlaceholderLines(modifier = Modifier.padding(top = 18.dp).fillMaxWidth(), widths = listOf(.62f, .86f, .7f))
        }
        item { WireImage("Video de muestra", modifier = Modifier.fillMaxWidth().height(104.dp)) }
        item { QuoteField("Nombre", modifier = Modifier.fillMaxWidth()) }
        item { QuoteField("WhatsApp", modifier = Modifier.fillMaxWidth()) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuoteField("Fecha", modifier = Modifier.weight(1f))
                QuoteField("Hora", modifier = Modifier.weight(1f))
            }
        }
        item { QuoteField("Qué buscas? (quinceañera, boda...)", modifier = Modifier.fillMaxWidth()) }
        item { ActionButton(text = "Reservar videollamada", onClick = {}, modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun AccountContent(onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Blush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = Ink)
                }
                PlaceholderLines(modifier = Modifier.weight(1f), widths = listOf(.58f, .85f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatPill("Perfil", modifier = Modifier.weight(1f))
                StatPill("Favoritos", modifier = Modifier.weight(1f))
                StatPill("Cotizaciones", modifier = Modifier.weight(1f))
            }
        }
        item { QuoteField("Nombre", modifier = Modifier.fillMaxWidth()) }
        item { QuoteField("WhatsApp", modifier = Modifier.fillMaxWidth()) }
        item { SectionDivider() }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { WireImage("Favorito", modifier = Modifier.weight(1f).aspectRatio(1f)) }
            }
        }
        item { ActionButton(text = "Cerrar sesión", onClick = onLogout, modifier = Modifier.fillMaxWidth(), filled = false) }
    }
}

@Composable
private fun DrawerOverlay(onClose: () -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(.82f)
                .fillMaxSize()
                .background(Color.White)
                .padding(18.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = Ink)
                }
                BrandMark()
                Spacer(Modifier.width(48.dp))
            }
            Spacer(Modifier.height(20.dp))
            listOf("Colección", "Citas", "Cotizaciones", "Pedidos especiales", "Preguntas frecuentes", "WhatsApp directo").forEachIndexed { index, label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (index) {
                            0 -> Icons.AutoMirrored.Outlined.ReceiptLong
                            1 -> Icons.Outlined.CalendarMonth
                            2 -> Icons.Outlined.FavoriteBorder
                            else -> Icons.AutoMirrored.Outlined.Send
                        },
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(label, color = SoftInk, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatPill("ES")
                StatPill("EN")
                Spacer(Modifier.weight(1f))
                StatPill("Tema")
            }
            ActionButton(text = "WhatsApp directo", onClick = {}, modifier = Modifier.padding(top = 22.dp).fillMaxWidth(), filled = false)
        }
        Box(
            modifier = Modifier
                .weight(.18f)
                .fillMaxSize()
                .background(Gold.copy(alpha = .28f))
        )
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