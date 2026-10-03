package com.creacionesnormita.mobile.features.admin

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.ProductoEscritura
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.core.network.CloudinaryUploader
import com.creacionesnormita.mobile.ui.components.ActionButton
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PantallaEditarProducto(productoId: Int?, onBack: () -> Unit) {
    val controller = remember { ProductoController() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val esNuevo = productoId == null

    var yaCargado by remember { mutableStateOf(esNuevo) }
    var guardando by remember { mutableStateOf(false) }
    var mostrarConfirmarEliminar by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(true) }
    var esVestidoDelDia by remember { mutableStateOf(false) }
    val imagenes = remember { mutableStateListOf<String>() }
    var subiendo by remember { mutableStateOf(0) } // cuántas fotos se están subiendo a Cloudinary ahora mismo
    val stockPorTalla = remember {
        mutableStateMapOf<Talla, String>().apply { Talla.entries.forEach { put(it, "0") } }
    }

    // Selector nativo de galería (sin permisos). Cada foto se sube a Cloudinary apenas se elige
    // y su URL se agrega al final del arreglo de imágenes.
    val selectorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            subiendo += uris.size
            scope.launch {
                var fallos = 0
                var ultimoError: String? = null
                uris.forEach { uri ->
                    CloudinaryUploader.subirImagen(context, uri)
                        .onSuccess { url -> imagenes.add(url) }
                        .onFailure { e ->
                            fallos++
                            ultimoError = e.message
                        }
                    subiendo--
                }
                if (fallos > 0) {
                    Toast.makeText(
                        context,
                        "No se pudieron subir $fallos foto(s): $ultimoError",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(productoId) {
        if (productoId != null) {
            controller.cargarProductoPorId(productoId)
        }
    }

    LaunchedEffect(controller.productoSeleccionado) {
        val producto = controller.productoSeleccionado
        if (producto != null && !yaCargado) {
            nombre = producto.nombre
            descripcion = producto.descripcion ?: ""
            precioTexto = String.format(Locale.US, "%.2f", producto.precio)
            color = producto.color ?: ""
            disponible = producto.disponible
            esVestidoDelDia = producto.destacado
            imagenes.clear()
            imagenes.addAll(producto.imagenes)
            producto.stockPorTalla.forEach { stockPorTalla[it.talla] = it.stock.toString() }
            yaCargado = true
        }
    }

    Scaffold(
        topBar = { AdminHeader(titulo = if (esNuevo) "Nuevo vestido" else "Editar vestido", onBack = onBack) },
        containerColor = Paper
    ) { padding ->
        if (!esNuevo && !yaCargado) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando...", color = SoftInk)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { AdminTextField(label = "Nombre", value = nombre, onValueChange = { nombre = it }) }
            item {
                AdminTextField(
                    label = "Descripción",
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    singleLine = false,
                    minLines = 3
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminTextField(
                        label = "Precio",
                        value = precioTexto,
                        onValueChange = { precioTexto = it },
                        modifier = Modifier.weight(1f),
                        soloNumeros = true
                    )
                    AdminTextField(label = "Color", value = color, onValueChange = { color = it }, modifier = Modifier.weight(1f))
                }
            }
            item {
                Column {
                    Text("FOTOS DEL VESTIDO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftInk)
                    Text(
                        "La primera foto es la portada. Usa las flechas para cambiar el orden del carrusel.",
                        fontSize = 11.sp,
                        color = SoftInk,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        imagenes.forEachIndexed { index, url ->
                            MiniaturaImagen(
                                url = url,
                                esPortada = index == 0,
                                puedeIzquierda = index > 0,
                                puedeDerecha = index < imagenes.lastIndex,
                                onQuitar = { imagenes.removeAt(index) },
                                onMoverIzquierda = {
                                    val actual = imagenes[index]
                                    imagenes[index] = imagenes[index - 1]
                                    imagenes[index - 1] = actual
                                },
                                onMoverDerecha = {
                                    val actual = imagenes[index]
                                    imagenes[index] = imagenes[index + 1]
                                    imagenes[index + 1] = actual
                                }
                            )
                        }
                        repeat(subiendo) {
                            Box(
                                modifier = Modifier
                                    .size(104.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Line.copy(alpha = .3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(26.dp),
                                    color = Marca,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            selectorGaleria.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = Marca, modifier = Modifier.size(18.dp))
                        Text(" Agregar fotos desde la galería", color = Marca, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { disponible = !disponible },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Visible en Colección", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("Si lo apagas, el vestido se oculta de la app para los clientes", fontSize = 11.sp, color = SoftInk)
                    }
                    Switch(
                        checked = disponible,
                        onCheckedChange = { disponible = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Marca)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { esVestidoDelDia = !esVestidoDelDia },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = esVestidoDelDia,
                        onCheckedChange = { esVestidoDelDia = it },
                        colors = CheckboxDefaults.colors(checkedColor = Marca)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Vestido del día", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(
                            "Se muestra en el carrusel de Inicio. Al marcarlo, se desmarca el anterior automáticamente.",
                            fontSize = 11.sp,
                            color = SoftInk
                        )
                    }
                }
            }
            item {
                Column {
                    Text("STOCK POR TALLA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftInk)
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Talla.entries.forEach { talla ->
                            AdminTextField(
                                label = talla.name,
                                value = stockPorTalla[talla] ?: "0",
                                onValueChange = { nuevo ->
                                    if (nuevo.isEmpty() || nuevo.all { it.isDigit() }) stockPorTalla[talla] = nuevo
                                },
                                modifier = Modifier.width(78.dp),
                                soloNumeros = true
                            )
                        }
                    }
                }
            }
            item {
                val puedeGuardar = !guardando && subiendo == 0 && nombre.isNotBlank() && precioTexto.toDoubleOrNull() != null
                ActionButton(
                    text = when {
                        subiendo > 0 -> "Subiendo fotos..."
                        guardando -> "Guardando..."
                        else -> "Guardar"
                    },
                    enabled = puedeGuardar,
                    onClick = {
                        scope.launch {
                            guardando = true
                            val datos = ProductoEscritura(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim().ifBlank { null },
                                precio = precioTexto.toDoubleOrNull() ?: 0.0,
                                imagenes = imagenes.map { it.trim() }.filter { it.isNotBlank() },
                                color = color.trim().ifBlank { null },
                                disponible = disponible
                            )
                            val stockFinal = stockPorTalla.mapValues { it.value.toIntOrNull() ?: 0 }

                            val idFinal: Int? = if (esNuevo) {
                                controller.crearProducto(datos, stockFinal)?.id
                            } else {
                                val actualizadoOk = controller.actualizarProducto(productoId!!, datos)
                                var stockOk = true
                                stockFinal.forEach { (talla, cantidad) ->
                                    if (!controller.actualizarStockTalla(productoId, talla, cantidad)) stockOk = false
                                }
                                if (actualizadoOk && stockOk) productoId else null
                            }

                            if (idFinal != null) {
                                if (esVestidoDelDia) {
                                    controller.marcarComoVestidoDelDia(idFinal)
                                } else if (!esNuevo) {
                                    controller.quitarVestidoDelDia(idFinal)
                                }
                                Toast.makeText(context, "Vestido guardado", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "No se pudo guardar: ${controller.errorAdmin}", Toast.LENGTH_LONG).show()
                            }
                            guardando = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (!esNuevo) {
                item {
                    ActionButton(
                        text = "Eliminar vestido",
                        filled = false,
                        onClick = { mostrarConfirmarEliminar = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (mostrarConfirmarEliminar && productoId != null) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarEliminar = false },
            title = { Text("¿Eliminar este vestido?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. El vestido y su stock se eliminarán por completo.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val ok = controller.eliminarProducto(productoId)
                        mostrarConfirmarEliminar = false
                        if (ok) {
                            Toast.makeText(context, "Vestido eliminado", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Eliminar", color = Marca, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun MiniaturaImagen(
    url: String,
    esPortada: Boolean,
    puedeIzquierda: Boolean,
    puedeDerecha: Boolean,
    onQuitar: () -> Unit,
    onMoverIzquierda: () -> Unit,
    onMoverDerecha: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(104.dp)) {
            AsyncImage(
                model = url,
                contentDescription = "Foto del vestido",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Line.copy(alpha = .3f))
            )
            if (esPortada) {
                Text(
                    "PORTADA",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .background(Marca, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            IconButton(
                onClick = onQuitar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = .55f), CircleShape)
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Quitar foto", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Row {
            IconButton(onClick = onMoverIzquierda, enabled = puedeIzquierda, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mover a la izquierda")
            }
            IconButton(onClick = onMoverDerecha, enabled = puedeDerecha, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mover a la derecha")
            }
        }
    }
}

@Composable
internal fun AdminTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    soloNumeros: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = if (soloNumeros) {
            androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
        } else {
            androidx.compose.foundation.text.KeyboardOptions.Default
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Marca,
            focusedLabelColor = Marca,
            cursorColor = Marca
        ),
        modifier = modifier.fillMaxWidth()
    )
}