package com.creacionesnormita.mobile.features.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.ui.components.StatPill

// Trabajo Guillermo: fila usada en la subpantalla con la lista completa de favoritos.
@Composable
internal fun FavoritoListaRow(
    producto: Producto,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = producto.imagenes.firstOrNull(),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Paper)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(producto.precioFormateado, fontSize = 12.sp, color = Marca, modifier = Modifier.padding(top = 3.dp))
                StatPill(
                    text = if (producto.stockPorTalla.sumOf { it.stock } > 0) "Disponible ahora" else "Agotado",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Quitar de favoritos",
                    tint = Marca
                )
            }
        }
    }
}

// Trabajo Guillermo: muestra en perfil los tres ultimos vestidos marcados como favoritos.
@Composable
internal fun UltimosFavoritosPerfil(
    favoritos: List<Producto>,
    cargando: Boolean,
    onProductoClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Últimos favoritos", fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) { index ->
                MiniFavoritoSlot(
                    producto = favoritos.getOrNull(index),
                    cargando = cargando,
                    onClick = {
                        favoritos.getOrNull(index)?.let { onProductoClick(it.id) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (!cargando && favoritos.isEmpty()) {
            Text("Marca vestidos con la estrella del catálogo para verlos aquí.", color = SoftInk, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MiniFavoritoSlot(
    producto: Producto?,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Paper)
            .then(if (producto != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            when {
                producto != null -> {
                    AsyncImage(
                        model = producto.imagenes.firstOrNull(),
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                cargando -> Text("...", color = SoftInk, fontSize = 12.sp)
                else -> Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Line)
            }
        }
        Text(
            text = producto?.nombre ?: "Vacío",
            color = if (producto != null) Ink else SoftInk,
            fontSize = 10.sp,
            fontWeight = if (producto != null) FontWeight.Bold else FontWeight.Normal,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
