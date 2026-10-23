package com.creacionesnormita.mobile.features.quote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.controller.CarritoController
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.core.abrirWhatsApp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.ItemCotizacion
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.CampoFormulario
import com.creacionesnormita.mobile.ui.components.FilterChip

@Composable
internal fun QuoteContent(perfilController: PerfilController) {
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
        item { CampoFormulario(label = "Nombre", value = nombre, onValueChange = { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoFormulario("WhatsApp", whatsapp, { whatsapp = it }, modifier = Modifier.weight(1f))
                CampoFormulario("Email", email, { email = it }, modifier = Modifier.weight(1f))
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
            CampoFormulario(
                "Fecha del evento + mín. según destino",
                fechaEvento,
                { fechaEvento = it }
            )
        }
        item {
            CampoFormulario(
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
                        abrirWhatsApp(context, mensaje)
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
