package com.creacionesnormita.mobile.features.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.Constantes
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AutoCarousel
import java.net.URLEncoder

@Composable
fun PantallaDetalleProducto(productoId: Int, onBack: () -> Unit) {
    val controller = remember { ProductoController() }

    LaunchedEffect(productoId) {
        controller.cargarProductoPorId(productoId)
    }

    Scaffold(
        topBar = {
            DetalleHeader(
                titulo = controller.productoSeleccionado?.nombre ?: "Detalle",
                onBack = onBack
            )
        },
        containerColor = Paper
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                controller.cargandoDetalle -> {
                    Text("Cargando...", modifier = Modifier.align(Alignment.Center), color = SoftInk)
                }
                controller.errorDetalle != null -> {
                    Text(
                        "No se pudo cargar el producto: ${controller.errorDetalle}",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = SoftInk
                    )
                }
                controller.productoSeleccionado != null -> {
                    DetalleContenido(producto = controller.productoSeleccionado!!)
                }
                else -> {
                    Text("Producto no encontrado", modifier = Modifier.align(Alignment.Center), color = SoftInk)
                }
            }
        }
    }
}

@Composable
private fun DetalleHeader(titulo: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White).statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Ink)
            }
            Text(
                titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Ink,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line))
    }
}

@Composable
private fun DetalleContenido(producto: Producto) {
    var tallaSeleccionada by remember { mutableStateOf<Talla?>(null) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AutoCarousel(
                imagenes = producto.imagenes,
                autoAvanzar = false, // en el detalle el usuario desliza manualmente
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(.9f)
                    .clip(RoundedCornerShape(18.dp))
            )
        }
        item {
            Column {
                Text(producto.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("$${producto.precio}", fontSize = 16.sp, color = Marca, modifier = Modifier.padding(top = 4.dp))
            }
        }
        producto.descripcion?.let { descripcion ->
            item {
                Text(descripcion, fontSize = 13.sp, color = SoftInk)
            }
        }
        item {
            Column {
                Text("TALLA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftInk)
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Talla.entries.forEach { talla ->
                        val stockDeEstaTalla = producto.stockPorTalla.firstOrNull { it.talla == talla }?.stock ?: 0
                        val disponible = stockDeEstaTalla > 0
                        val seleccionada = tallaSeleccionada == talla

                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        seleccionada -> Marca
                                        !disponible -> Line.copy(alpha = .4f)
                                        else -> Line
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(
                                    if (seleccionada) Marca.copy(alpha = .12f) else Color.White,
                                    RoundedCornerShape(10.dp)
                                )
                                .then(
                                    if (disponible) Modifier.clickable { tallaSeleccionada = talla } else Modifier
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                talla.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (disponible) Ink else Line
                            )
                        }
                    }
                }
                val stockSeleccionado = tallaSeleccionada?.let { talla ->
                    producto.stockPorTalla.firstOrNull { it.talla == talla }?.stock
                }
                if (stockSeleccionado != null) {
                    Text(
                        "Disponibles: $stockSeleccionado",
                        fontSize = 11.sp,
                        color = SoftInk,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
        item {
            ActionButton(
                text = "Cotizar por WhatsApp",
                onClick = {
                    val mensaje = construirMensajeWhatsApp(producto, tallaSeleccionada)
                    val uri = Uri.parse(
                        "https://wa.me/${Constantes.WHATSAPP_NUMERO}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun construirMensajeWhatsApp(producto: Producto, talla: Talla?): String {
    return buildString {
        append("¡Hola! Me interesa cotizar este vestido:\n\n")
        append("Vestido: ${producto.nombre}\n")
        if (talla != null) append("Talla: ${talla.name}\n")
        append("Precio: $${producto.precio}\n")
    }
}