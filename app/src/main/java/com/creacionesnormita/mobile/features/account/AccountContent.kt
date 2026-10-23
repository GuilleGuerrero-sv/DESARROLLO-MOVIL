package com.creacionesnormita.mobile.features.account

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.FavoritosController
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.controller.ProductoController
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AvatarPerfil
import com.creacionesnormita.mobile.ui.components.RoleBadge
import com.creacionesnormita.mobile.ui.components.SectionDivider
import kotlinx.coroutines.launch

@Composable
internal fun AccountContent(
    perfilController: PerfilController,
    onProductoClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val perfil = perfilController.perfilActual
    val context = LocalContext.current
    val productoController = remember { ProductoController() }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var subSeccionCuenta by rememberSaveable { mutableStateOf("perfil") }
    val userId = perfilController.currentUserId ?: perfil?.id

    LaunchedEffect(userId) {
        FavoritosController.cargar(context, userId)
        productoController.cargarProductos()
    }

    val productosFavoritos = FavoritosController.productosFavoritos(productoController.productos)
    val ultimosFavoritos = FavoritosController.productosFavoritos(productoController.productos, limite = 3)

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
                    AvatarPerfil(size = 76.dp, iconSize = 36.dp)

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
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OpcionCuentaCard(
                    label = "Perfil",
                    icon = Icons.Outlined.Person,
                    seleccionado = subSeccionCuenta == "perfil",
                    onClick = { subSeccionCuenta = "perfil" },
                    modifier = Modifier.weight(1f)
                )
                OpcionCuentaCard(
                    label = "Favoritos",
                    icon = Icons.Outlined.FavoriteBorder,
                    seleccionado = subSeccionCuenta == "favoritos",
                    onClick = { subSeccionCuenta = "favoritos" },
                    modifier = Modifier.weight(1f)
                )
                OpcionCuentaCard(
                    label = "Cotizaciones",
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    seleccionado = subSeccionCuenta == "cotizaciones",
                    onClick = { subSeccionCuenta = "cotizaciones" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (subSeccionCuenta) {
            "favoritos" -> {
                item {
                    PanelInformativo(
                        icon = Icons.Outlined.FavoriteBorder,
                        titulo = "Tus vestidos favoritos",
                        descripcion = "Lista completa de vestidos marcados con estrella en el catálogo."
                    )
                }
                when {
                    productoController.cargandoLista -> {
                        item {
                            Text("Cargando favoritos...", color = SoftInk, fontSize = 12.sp)
                        }
                    }
                    FavoritosController.idsFavoritos.isEmpty() -> {
                        item {
                            Text("Todavía no has agregado vestidos a favoritos.", color = SoftInk, fontSize = 12.sp)
                        }
                    }
                    productosFavoritos.isEmpty() -> {
                        item {
                            Text("No se encontraron productos favoritos disponibles en este momento.", color = SoftInk, fontSize = 12.sp)
                        }
                    }
                    else -> {
                        items(productosFavoritos, key = { it.id }) { producto ->
                            FavoritoListaRow(
                                producto = producto,
                                onClick = { onProductoClick(producto.id) },
                                onRemove = {
                                    FavoritosController.alternar(context, userId, producto.id)
                                    Toast.makeText(context, "Quitado de favoritos", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
            "cotizaciones" -> {
                item {
                    PanelInformativo(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        titulo = "Historial de cotizaciones",
                        descripcion = "Tus solicitudes de cotización enviadas por WhatsApp se registrarán aquí."
                    )
                }
            }
            else -> {
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
                    UltimosFavoritosPerfil(
                        favoritos = ultimosFavoritos,
                        cargando = productoController.cargandoLista,
                        onProductoClick = onProductoClick
                    )
                }

                item {
                    ActionButton(
                        text = "Editar datos autorizados",
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            ActionButton(
                text = "Cerrar sesión",
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                filled = false
            )
        }

        item {
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFFDC2626)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                )
            ) {
                Text(
                    text = "ELIMINAR CUENTA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }

    val scope = rememberCoroutineScope()

    if (showEditDialog) {
        EditarPerfilDialog(
            perfil = perfil,
            onDismiss = { showEditDialog = false },
            onSave = { nuevoNombre, nuevoCelular, nuevoOtro ->
                scope.launch {
                    val ok = perfilController.actualizarPerfil(
                        nombre = nuevoNombre,
                        celular = nuevoCelular,
                        otroContacto = nuevoOtro
                    )
                    if (ok) {
                        Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmDeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                scope.launch {
                    val ok = perfilController.eliminarCuentaActual()
                    if (ok) {
                        Toast.makeText(context, "Tu cuenta ha sido eliminada.", Toast.LENGTH_LONG).show()
                        onLogout()
                    } else {
                        Toast.makeText(context, "No se pudo eliminar la cuenta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun OpcionCuentaCard(
    label: String,
    icon: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) Blush else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (seleccionado) Marca else Line),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (seleccionado) Marca else Ink,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) Marca else Ink,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PanelInformativo(icon: ImageVector, titulo: String, descripcion: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Marca, modifier = Modifier.size(32.dp))
        Text(titulo, fontWeight = FontWeight.Bold, color = Ink)
        Text(descripcion, fontSize = 12.sp, color = SoftInk, textAlign = TextAlign.Center)
    }
}
