package com.creacionesnormita.mobile.features.collection

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.controller.FavoritosController
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.ui.components.FilterChip
import com.creacionesnormita.mobile.ui.components.StatPill

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
internal fun CollectionContent(
    perfilController: PerfilController,
    onProductoClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val productoController = remember { ProductoController() }
    var filtrosActivos by remember { mutableStateOf<Set<FiltroColeccion>>(emptySet()) }
    val userId = perfilController.currentUserId ?: perfilController.perfilActual?.id

    LaunchedEffect(Unit) {
        productoController.cargarProductos()
    }

    // Trabajo Guillermo: carga favoritos de la cuenta actual para pintar la estrella del catalogo.
    LaunchedEffect(userId) {
        FavoritosController.cargar(context, userId)
    }

    val productosFiltrados = productoController.productos.filter { it.cumpleFiltros(filtrosActivos) }
    val favoritos = FavoritosController.idsFavoritos

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
                        DressCard(
                            producto = producto,
                            esFavorito = favoritos.contains(producto.id),
                            onFavoriteClick = {
                                val agregado = FavoritosController.alternar(context, userId, producto.id)
                                val mensaje = if (agregado) "Agregado a favoritos" else "Quitado de favoritos"
                                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            },
                            onClick = { onProductoClick(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DressCard(
    producto: Producto,
    esFavorito: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val stockTotal = producto.stockPorTalla.sumOf { it.stock }
    val hayStock = producto.disponible && stockTotal > 0

    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(.82f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            AsyncImage(
                model = producto.imagenes.firstOrNull(),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Trabajo Guillermo: boton de estrella para agregar o quitar del listado de favoritos.
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = .92f))
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (esFavorito) Marca else Ink,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
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
