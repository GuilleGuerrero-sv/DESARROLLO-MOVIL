package com.creacionesnormita.mobile.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.design.Gold
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.ui.components.StatPill

@Composable
fun PantallaGestionCatalogo(
    onBack: () -> Unit,
    onNuevoProducto: () -> Unit,
    onEditarProducto: (Int) -> Unit,
) {
    val controller = remember { ProductoController() }

    LaunchedEffect(Unit) {
        controller.cargarProductosAdmin()
    }

    Scaffold(
        topBar = {
            AdminHeader(
                titulo = "Gestión de Catálogo",
                onBack = onBack,
                acciones = {
                    TextButton(onClick = onNuevoProducto) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = Marca, modifier = Modifier.size(18.dp))
                        Text(" Agregar", color = Marca, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = Paper
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                controller.cargandoAdmin -> {
                    Text("Cargando catálogo...", modifier = Modifier.align(Alignment.Center), color = SoftInk)
                }
                controller.errorAdmin != null -> {
                    Text(
                        "No se pudo cargar: ${controller.errorAdmin}",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = SoftInk
                    )
                }
                controller.productosAdmin.isEmpty() -> {
                    Text(
                        "Aún no hay vestidos. Toca \"Agregar\" para crear el primero.",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = SoftInk
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(controller.productosAdmin) { producto ->
                            ProductoAdminRow(producto = producto, onClick = { onEditarProducto(producto.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoAdminRow(producto: Producto, onClick: () -> Unit) {
    val stockTotal = producto.stockPorTalla.sumOf { it.stock }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = producto.imagenes.firstOrNull(),
            contentDescription = producto.nombre,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Line.copy(alpha = .2f))
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (producto.destacado) {
                    Icon(Icons.Filled.Star, contentDescription = "Vestido del día", tint = Gold, modifier = Modifier.size(14.dp))
                    Row(modifier = Modifier.padding(start = 4.dp)) {}
                }
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Ink)
            }
            Text("${producto.precioFormateado} · Stock total: $stockTotal", fontSize = 11.sp, color = SoftInk)
        }
        StatPill(if (producto.disponible) "Visible" else "Oculto")
    }
}

@Composable
internal fun AdminHeader(
    titulo: String,
    onBack: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {},
) {
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
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
            acciones()
            Spacer(Modifier.width(8.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line))
    }
}