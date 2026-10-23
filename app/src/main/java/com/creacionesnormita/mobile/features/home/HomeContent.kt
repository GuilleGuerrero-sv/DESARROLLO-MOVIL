package com.creacionesnormita.mobile.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.abrirWhatsApp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AutoCarousel

/** Atajo rápido de la pantalla de inicio: icono + texto + acción. */
private data class Atajo(
    val titulo: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

@Composable
internal fun HomeContent(
    onGoCollection: () -> Unit,
    onGoAppointments: () -> Unit,
    onGoQuote: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val productoController = remember { ProductoController() }

    // Atajos rápidos: cada uno con su icono, texto y acción.
    val atajos = listOf(
        Atajo("Colección", Icons.Outlined.StarBorder, onGoCollection),
        Atajo("Citas", Icons.Outlined.CalendarMonth, onGoAppointments),
        Atajo("Cotizar", Icons.AutoMirrored.Outlined.ReceiptLong, onGoQuote),
        Atajo("WhatsApp", Icons.Outlined.Phone) { abrirWhatsApp(context) }
    )

    LaunchedEffect(Unit) {
        productoController.cargarVestidoDelDia()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(text = "Ver colección", onClick = onGoCollection, modifier = Modifier.weight(1f))
                ActionButton(
                    text = "WhatsApp",
                    onClick = { abrirWhatsApp(context) },
                    modifier = Modifier.weight(1f),
                    filled = false
                )
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

        // ---- Atajos rápidos (con funcionalidad real) ----
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                atajos.forEach { atajo ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Line, RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .clickable(onClick = atajo.onClick)
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = atajo.icono,
                            contentDescription = atajo.titulo,
                            tint = Marca,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            atajo.titulo,
                            color = Ink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // ---- Video de muestra (reproductor real, bien visible) ----
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Conoce nuestro trabajo",
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                VideoMuestra()
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
                Text(
                    "Reserva una videollamada y te mostramos los vestidos en vivo.",
                    color = SoftInk,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 14.dp)
                )
                ActionButton(
                    text = "Agendar cita en videollamada",
                    onClick = onGoAppointments,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(.72f)
                        .heightIn(min = 56.dp)
                )
            }
        }
    }
}
