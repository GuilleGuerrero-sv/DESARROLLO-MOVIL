package com.creacionesnormita.mobile.features.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.controller.CarritoController
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.Constantes
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Gold
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.Sage
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.ItemCotizacion
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.core.sample.serviciosDestacados
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AutoCarousel
import com.creacionesnormita.mobile.ui.components.BrandMark
import com.creacionesnormita.mobile.ui.components.FilterChip
import com.creacionesnormita.mobile.ui.components.PlaceholderLines
import com.creacionesnormita.mobile.ui.components.RoleBadge
import com.creacionesnormita.mobile.ui.components.SectionDivider
import com.creacionesnormita.mobile.ui.components.StatPill
import com.creacionesnormita.mobile.ui.components.WireImage
import kotlinx.coroutines.launch
import java.net.URLEncoder

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
                    onProductoClick = onProductoClick
                )
                MainTab.Collection -> CollectionContent(onProductoClick = onProductoClick)
                MainTab.Quote -> QuoteContent(perfilController = perfilController)
                MainTab.Appointments -> AppointmentContent()
                MainTab.Account -> AccountContent(
                    perfilController = perfilController,
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

            if (mostrarMenu) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "Más", tint = Ink)
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
        }
        SectionDivider()
    }
}

@Composable
private fun HomeContent(
    onGoCollection: () -> Unit,
    onGoAppointments: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    val productoController = remember { ProductoController() }

    LaunchedEffect(Unit) {
        productoController.cargarVestidoDelDia()
    }

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
            val vestido = productoController.vestidoDelDia
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(.98f)
                    .clip(RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp, bottomStart = 22.dp, bottomEnd = 22.dp))
                    .border(1.dp, Ink, RoundedCornerShape(topStart = 140.dp, topEnd = 140.dp, bottomStart = 22.dp, bottomEnd = 22.dp))
                    .background(Color.White)
                    .then(
                        if (vestido != null) Modifier.clickable { onProductoClick(vestido.id) } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
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
                    Text(vestido.precioFormateado, color = Marca, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
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

/** Filtros combinables de Colección: 'Disponible ahora' + cualquier cantidad de tallas a la vez. */
private sealed class FiltroColeccion {
    data object Disponible : FiltroColeccion()
    data class PorTalla(val talla: Talla) : FiltroColeccion()
}

private fun Producto.cumpleFiltros(filtros: Set<FiltroColeccion>): Boolean {
    if (filtros.isEmpty()) return true // "Todos": sin filtros activos

    val tallasPedidas = filtros.filterIsInstance<FiltroColeccion.PorTalla>().map { it.talla }
    val pideDisponible = filtros.contains(FiltroColeccion.Disponible)

    val cumpleTalla = tallasPedidas.isEmpty() || tallasPedidas.any { talla ->
        (stockPorTalla.firstOrNull { it.talla == talla }?.stock ?: 0) > 0
    }
    val cumpleDisponible = !pideDisponible || stockPorTalla.sumOf { it.stock } > 0

    return cumpleTalla && cumpleDisponible
}

@Composable
private fun CollectionContent(onProductoClick: (Int) -> Unit) {
    val productoController = remember { ProductoController() }
    var filtrosActivos by remember { mutableStateOf<Set<FiltroColeccion>>(emptySet()) }

    LaunchedEffect(Unit) {
        productoController.cargarProductos()
    }

    val productosFiltrados = productoController.productos.filter { it.cumpleFiltros(filtrosActivos) }

    Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                text = "Todos",
                selected = filtrosActivos.isEmpty(),
                onClick = { filtrosActivos = emptySet() }
            )
            FilterChip(
                text = "Disponible ahora",
                selected = filtrosActivos.contains(FiltroColeccion.Disponible),
                onClick = {
                    filtrosActivos = if (filtrosActivos.contains(FiltroColeccion.Disponible)) {
                        filtrosActivos - FiltroColeccion.Disponible
                    } else {
                        filtrosActivos + FiltroColeccion.Disponible
                    }
                }
            )
            Talla.entries.forEach { talla ->
                val filtroTalla = FiltroColeccion.PorTalla(talla)
                FilterChip(
                    text = talla.name,
                    selected = filtrosActivos.contains(filtroTalla),
                    onClick = {
                        filtrosActivos = if (filtrosActivos.contains(filtroTalla)) {
                            filtrosActivos - filtroTalla
                        } else {
                            filtrosActivos + filtroTalla
                        }
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        when {
            productoController.cargandoLista -> {
                Text("Cargando colección...", color = SoftInk, fontSize = 12.sp)
            }
            productoController.errorLista != null -> {
                Text("No se pudo cargar la colección: ${productoController.errorLista}", color = SoftInk, fontSize = 12.sp)
            }
            productoController.productos.isEmpty() -> {
                Text("Aún no hay productos publicados", color = SoftInk, fontSize = 12.sp)
            }
            productosFiltrados.isEmpty() -> {
                Text("Ningún vestido coincide con los filtros seleccionados", color = SoftInk, fontSize = 12.sp)
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        DressCard(producto = producto, onClick = { onProductoClick(producto.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DressCard(producto: Producto, onClick: () -> Unit) {
    val stockTotal = producto.stockPorTalla.sumOf { it.stock }
    val hayStock = producto.disponible && stockTotal > 0

    Column(modifier = Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = producto.imagenes.firstOrNull(),
            contentDescription = producto.nombre,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(.82f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        )
        Text(
            producto.nombre,
            color = SoftInk,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            producto.precioFormateado,
            color = Marca,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
        StatPill(
            text = if (hayStock) "Disponible ahora" else "Agotado",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun QuoteContent(perfilController: PerfilController) {
    val context = LocalContext.current
    val items = CarritoController.items

    var nombre by rememberSaveable { mutableStateOf("") }
    var whatsapp by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var tipoEnvio by rememberSaveable { mutableStateOf("Nacional") }
    var fechaEvento by rememberSaveable { mutableStateOf("") }
    var notas by rememberSaveable { mutableStateOf("") }
    var yaPrecargado by rememberSaveable { mutableStateOf(false) }

    // Precarga una sola vez con los datos de la cuenta, apenas el perfil esté disponible.
    // Después de eso, el usuario puede editarlos libremente sin que se vuelvan a sobreescribir.
    LaunchedEffect(perfilController.perfilActual) {
        val perfil = perfilController.perfilActual
        if (perfil != null && !yaPrecargado) {
            nombre = perfil.nombre
            whatsapp = perfil.celular
            email = perfilController.userEmail
            yaPrecargado = true
        }
    }

    val puedeEnviar = items.isNotEmpty() && nombre.isNotBlank() && whatsapp.isNotBlank()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (items.isEmpty()) {
            item {
                Text(
                    "Tu lista de cotización está vacía. Agrega vestidos desde la Colección.",
                    color = SoftInk,
                    fontSize = 12.sp
                )
            }
        } else {
            items(items, key = { "${it.producto.id}-${it.talla}" }) { item ->
                CarritoItemRow(item = item)
            }
        }
        item { QuoteTextField(label = "Nombre", value = nombre, onValueChange = { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuoteTextField("WhatsApp", whatsapp, { whatsapp = it }, modifier = Modifier.weight(1f))
                QuoteTextField("Email", email, { email = it }, modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    text = "Nacional",
                    selected = tipoEnvio == "Nacional",
                    onClick = { tipoEnvio = "Nacional" },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    text = "Internacional",
                    selected = tipoEnvio == "Internacional",
                    onClick = { tipoEnvio = "Internacional" },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            QuoteTextField(
                "Fecha del evento + mín. según destino",
                fechaEvento,
                { fechaEvento = it }
            )
        }
        item {
            QuoteTextField(
                "Notas",
                notas,
                { notas = it },
                singleLine = false,
                minLines = 3
            )
        }
        item {
            Column {
                ActionButton(
                    text = "Enviar cotización",
                    enabled = puedeEnviar,
                    onClick = {
                        val mensaje = construirMensajeCotizacion(
                            items = items,
                            nombre = nombre,
                            whatsapp = whatsapp,
                            email = email,
                            tipoEnvio = tipoEnvio,
                            fechaEvento = fechaEvento,
                            notas = notas
                        )
                        val uri = Uri.parse(
                            "https://wa.me/${Constantes.WHATSAPP_NUMERO}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        CarritoController.limpiar()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!puedeEnviar) {
                    Text(
                        "Agrega al menos un vestido, tu nombre y tu WhatsApp para continuar",
                        fontSize = 11.sp,
                        color = SoftInk,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CarritoItemRow(item: ItemCotizacion) {
    val stockMax = item.producto.stockPorTalla.firstOrNull { it.talla == item.talla }?.stock ?: 0
    val enElLimite = item.cantidad >= stockMax

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = item.producto.imagenes.firstOrNull(),
                contentDescription = item.producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.producto.nombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Talla: ${item.talla.name} · ${item.producto.precioFormateado}", fontSize = 11.sp, color = SoftInk)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { CarritoController.decrementar(item) }, modifier = Modifier.size(28.dp)) {
                    Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
                Text("${item.cantidad}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                IconButton(
                    onClick = { CarritoController.incrementar(item) },
                    enabled = !enElLimite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (enElLimite) Line else Ink)
                }
            }
            IconButton(onClick = { CarritoController.quitar(item) }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Close, contentDescription = "Quitar", tint = SoftInk, modifier = Modifier.size(18.dp))
            }
        }
        if (enElLimite) {
            Text(
                "Solo quedan $stockMax disponibles en talla ${item.talla.name}",
                fontSize = 10.sp,
                color = Marca,
                modifier = Modifier.padding(start = 86.dp, top = 2.dp)
            )
        }
    }
}

@Composable
private fun QuoteTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Marca,
            unfocusedBorderColor = Line,
            focusedLabelColor = Marca,
            cursorColor = Marca
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun QuoteField(label: String, modifier: Modifier = Modifier, minHeight: Dp = 54.dp) {
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

private fun construirMensajeCotizacion(
    items: List<ItemCotizacion>,
    nombre: String,
    whatsapp: String,
    email: String,
    tipoEnvio: String,
    fechaEvento: String,
    notas: String,
): String = buildString {
    append("¡Hola! Quiero cotizar lo siguiente:\n\n")
    items.forEach { item ->
        append("• ${item.producto.nombre} — Talla ${item.talla.name} x${item.cantidad} (${item.producto.precioFormateado} c/u)\n")
    }
    append("\nNombre: $nombre\n")
    append("WhatsApp: $whatsapp\n")
    if (email.isNotBlank()) append("Email: $email\n")
    append("Envío: $tipoEnvio\n")
    if (fechaEvento.isNotBlank()) append("Fecha del evento: $fechaEvento\n")
    if (notas.isNotBlank()) append("Notas: $notas\n")
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
private fun AccountContent(
    perfilController: PerfilController,
    onLogout: () -> Unit
) {
    val perfil = perfilController.perfilActual
    val context = LocalContext.current
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Blush),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!perfil?.foto_url.isNullOrBlank()) {
                            AsyncImage(
                                model = perfil.foto_url,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Ink, modifier = Modifier.size(36.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = perfil?.nombre ?: "Cargando usuario...",
                            color = Ink,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = perfilController.userEmail,
                            color = SoftInk,
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = Marca, modifier = Modifier.size(14.dp))
                            Text(
                                text = perfil?.celular?.ifBlank { "WhatsApp no registrado" } ?: "Sin número",
                                color = SoftInk,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Role Badge
                        RoleBadge(rol = perfil?.rol ?: Perfil.ROL_CLIENTE, modifier = Modifier.padding(top = 8.dp))
                    }

                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar Perfil", tint = Marca)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatPill("Mi Perfil", modifier = Modifier.weight(1f))
                StatPill("Mis Favoritos", modifier = Modifier.weight(1f))
                StatPill("Mis Cotizaciones", modifier = Modifier.weight(1f))
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Información de la cuenta", fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp)
                SectionDivider()
                Text("Nombre: ${perfil?.nombre ?: "-"}", fontSize = 13.sp, color = SoftInk)
                Text("WhatsApp / Celular: ${perfil?.celular ?: "-"}", fontSize = 13.sp, color = SoftInk)
                Text("Otro contacto: ${perfil?.otro_contacto ?: "No especificado"}", fontSize = 13.sp, color = SoftInk)
                Text("Fecha Nacimiento: ${perfil?.fecha_nacimiento ?: "-"}", fontSize = 13.sp, color = SoftInk)
                Text("Rol autorizado: ${perfil?.rol ?: Perfil.ROL_CLIENTE}", fontSize = 13.sp, color = Marca, fontWeight = FontWeight.Bold)
            }
        }

        item {
            ActionButton(
                text = "Editar datos autorizados",
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ActionButton(
                text = "Cerrar sesión",
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                filled = false
            )
        }
    }

    val scope = rememberCoroutineScope()

    if (showEditDialog) {
        EditarPerfilDialog(
            perfil = perfil,
            onDismiss = { showEditDialog = false },
            onSave = { nuevoNombre, nuevoCelular, nuevoOtro, nuevaFoto ->
                scope.launch {
                    val ok = perfilController.actualizarPerfil(
                        nombre = nuevoNombre,
                        celular = nuevoCelular,
                        otroContacto = nuevoOtro,
                        fotoUrl = nuevaFoto
                    )
                    if (ok) {
                        Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun EditarPerfilDialog(
    perfil: Perfil?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(perfil?.nombre.orEmpty()) }
    var celular by rememberSaveable { mutableStateOf(perfil?.celular.orEmpty()) }
    var otroContacto by rememberSaveable { mutableStateOf(perfil?.otro_contacto.orEmpty()) }
    var fotoUrl by rememberSaveable { mutableStateOf(perfil?.foto_url.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar perfil", fontWeight = FontWeight.Bold, color = Ink) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = celular,
                    onValueChange = { celular = it },
                    label = { Text("WhatsApp / Celular*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = otroContacto,
                    onValueChange = { otroContacto = it },
                    label = { Text("Otro contacto (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fotoUrl,
                    onValueChange = { fotoUrl = it },
                    label = { Text("URL de foto de perfil (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nombre, celular, otroContacto, fotoUrl) }
            ) {
                Text("Guardar", color = Marca, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SoftInk)
            }
        }
    )
}

@Composable
private fun DrawerOverlay(
    perfil: Perfil?,
    userEmail: String,
    onClose: () -> Unit,
    onOpenGestionCatalogo: () -> Unit,
    onOpenGestionUsuarios: () -> Unit
) {
    val esEmpleadoOrAdmin = perfil?.esEmpleado == true
    val esAdmin = perfil?.esAdmin == true

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(.85f)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = Ink)
                }
                BrandMark()
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            // User Info Header in Drawer
            Card(
                colors = CardDefaults.cardColors(containerColor = Blush.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Blush),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!perfil?.foto_url.isNullOrBlank()) {
                            AsyncImage(
                                model = perfil?.foto_url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Ink)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            perfil?.nombre ?: "Cliente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Ink
                        )
                        RoleBadge(rol = perfil?.rol ?: Perfil.ROL_CLIENTE)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Secciones Estándar
            val opcionesStandard = listOf("Colección", "Citas", "Cotizaciones", "Preguntas frecuentes", "WhatsApp directo")
            opcionesStandard.forEachIndexed { index, label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClose() }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (index) {
                            0 -> Icons.Outlined.StarBorder
                            1 -> Icons.Outlined.CalendarMonth
                            2 -> Icons.AutoMirrored.Outlined.ReceiptLong
                            else -> Icons.AutoMirrored.Outlined.Send
                        },
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(label, color = SoftInk, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp))
                }
            }

            // --- Secciones por Rol ---
            if (esEmpleadoOrAdmin) {
                SectionDivider()
                Text(
                    "GESTIÓN DE EMPLEADO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Marca,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenGestionCatalogo() }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Inventory, contentDescription = null, tint = Marca, modifier = Modifier.size(20.dp))
                    Text("Gestión de Catálogo", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
                }
            }

            if (esAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenGestionUsuarios() }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                    Text("Gestión de Clientes y Usuarios", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
                }
            }

            Spacer(Modifier.weight(1f))
            ActionButton(text = "WhatsApp directo", onClick = {}, modifier = Modifier.fillMaxWidth(), filled = false)
        }
        Box(
            modifier = Modifier
                .weight(.15f)
                .fillMaxSize()
                .background(Gold.copy(alpha = .28f))
                .clickable { onClose() }
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