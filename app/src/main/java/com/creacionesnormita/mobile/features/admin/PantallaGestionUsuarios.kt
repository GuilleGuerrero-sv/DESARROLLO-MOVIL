package com.creacionesnormita.mobile.features.admin

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.ui.components.FilterChip
import com.creacionesnormita.mobile.ui.components.RoleBadge
import kotlinx.coroutines.launch
import java.text.Normalizer

/** Quita acentos y mayúsculas para que "jose" encuentre "José". */
private fun String.normalizar(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .trim()

/** Agrupa variantes como "admin"/"Administrador" o "empleado"/"Empleado" en uno de los 3 roles oficiales. */
private fun Perfil.rolNormalizado(): String = when {
    esAdmin -> Perfil.ROL_ADMINISTRADOR
    esEmpleado -> Perfil.ROL_EMPLEADO
    else -> Perfil.ROL_CLIENTE
}

@Composable
fun PantallaGestionUsuarios(onBack: () -> Unit) {
    val controller = remember { PerfilController() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val miPropioId = controller.currentUserId

    var busqueda by rememberSaveable { mutableStateOf("") }
    var rolesSeleccionados by remember { mutableStateOf<Set<String>>(emptySet()) }
    var soloBloqueados by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        controller.cargarTodosLosUsuarios()
    }

    // Se recalcula solo cuando cambia la lista o algún filtro. Orden alfabético por nombre.
    val usuariosFiltrados = remember(controller.usuariosRegistrados, busqueda, rolesSeleccionados, soloBloqueados) {
        val textoBuscado = busqueda.normalizar()
        controller.usuariosRegistrados
            .filter { usuario ->
                (textoBuscado.isEmpty() || usuario.nombre.normalizar().contains(textoBuscado)) &&
                        (rolesSeleccionados.isEmpty() || usuario.rolNormalizado() in rolesSeleccionados) &&
                        (!soloBloqueados || !usuario.activo)
            }
            .sortedBy { it.nombre.normalizar() }
    }
    val hayFiltrosActivos = busqueda.isNotBlank() || rolesSeleccionados.isNotEmpty() || soloBloqueados

    Scaffold(
        topBar = { AdminHeader(titulo = "Gestión de Clientes y Usuarios", onBack = onBack) },
        containerColor = Paper
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- Buscador + filtros (fijos arriba, no se desplazan con la lista) ---
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BuscadorUsuarios(valor = busqueda, onCambio = { busqueda = it })

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        text = "Todos",
                        selected = rolesSeleccionados.isEmpty() && !soloBloqueados,
                        onClick = {
                            rolesSeleccionados = emptySet()
                            soloBloqueados = false
                        }
                    )
                    listOf(Perfil.ROL_CLIENTE, Perfil.ROL_EMPLEADO, Perfil.ROL_ADMINISTRADOR).forEach { rol ->
                        FilterChip(
                            text = rol,
                            selected = rol in rolesSeleccionados,
                            onClick = {
                                rolesSeleccionados = if (rol in rolesSeleccionados) {
                                    rolesSeleccionados - rol
                                } else {
                                    rolesSeleccionados + rol
                                }
                            }
                        )
                    }
                    FilterChip(
                        text = "Bloqueados",
                        selected = soloBloqueados,
                        onClick = { soloBloqueados = !soloBloqueados }
                    )
                }

                if (controller.usuariosRegistrados.isNotEmpty()) {
                    Text(
                        text = "Mostrando ${usuariosFiltrados.size} de ${controller.usuariosRegistrados.size} usuarios",
                        fontSize = 11.sp,
                        color = SoftInk
                    )
                }
            }

            // --- Resultado ---
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    controller.cargandoUsuarios -> {
                        Text("Cargando usuarios...", modifier = Modifier.align(Alignment.Center), color = SoftInk)
                    }
                    controller.errorUsuarios != null && controller.usuariosRegistrados.isEmpty() -> {
                        Text(
                            "No se pudo cargar: ${controller.errorUsuarios}",
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            color = SoftInk
                        )
                    }
                    controller.usuariosRegistrados.isEmpty() -> {
                        Text("No hay usuarios registrados", modifier = Modifier.align(Alignment.Center), color = SoftInk)
                    }
                    usuariosFiltrados.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Ningún usuario coincide con tu búsqueda", color = SoftInk, fontSize = 13.sp)
                            if (hayFiltrosActivos) {
                                TextButton(onClick = {
                                    busqueda = ""
                                    rolesSeleccionados = emptySet()
                                    soloBloqueados = false
                                }) {
                                    Text("Limpiar filtros", color = Marca, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(usuariosFiltrados, key = { it.id }) { usuario ->
                                UsuarioAdminCard(
                                    usuario = usuario,
                                    esUnoMismo = usuario.id == miPropioId,
                                    onCambiarRol = { nuevoRol ->
                                        scope.launch {
                                            val ok = controller.cambiarRolUsuario(usuario.id, nuevoRol)
                                            if (ok) {
                                                Toast.makeText(context, "Rol actualizado a $nuevoRol", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No se pudo cambiar el rol: ${controller.errorUsuarios}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onCambiarEstado = { nuevoEstado ->
                                        scope.launch {
                                            val ok = controller.cambiarEstadoUsuario(usuario.id, nuevoEstado)
                                            if (ok) {
                                                val mensaje = if (nuevoEstado) "Usuario reactivado" else "Usuario bloqueado"
                                                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No se pudo cambiar el estado: ${controller.errorUsuarios}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuscadorUsuarios(valor: String, onCambio: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        placeholder = { Text("Buscar por nombre", fontSize = 13.sp, color = SoftInk) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = SoftInk) },
        trailingIcon = {
            if (valor.isNotEmpty()) {
                IconButton(onClick = { onCambio("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Borrar búsqueda", tint = SoftInk, modifier = Modifier.size(18.dp))
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Marca,
            unfocusedBorderColor = Line,
            cursorColor = Marca
        )
    )
}

@Composable
private fun UsuarioAdminCard(
    usuario: Perfil,
    esUnoMismo: Boolean,
    onCambiarRol: (String) -> Unit,
    onCambiarEstado: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink)
                Text("Tel: ${usuario.celular.ifBlank { "—" }}", fontSize = 11.sp, color = SoftInk)
            }
            if (!usuario.activo) {
                Text("BLOQUEADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Marca)
            }
        }

        RoleBadge(rol = usuario.rol, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))

        if (esUnoMismo) {
            Text(
                "No puedes modificar tu propio rol ni bloquearte a ti mismo",
                fontSize = 11.sp,
                color = SoftInk
            )
        } else {
            Text("CAMBIAR ROL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SoftInk)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(Perfil.ROL_CLIENTE, Perfil.ROL_EMPLEADO, Perfil.ROL_ADMINISTRADOR).forEach { rolOpcion ->
                    TextButton(
                        onClick = { onCambiarRol(rolOpcion) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            rolOpcion.take(5),
                            fontSize = 10.sp,
                            fontWeight = if (usuario.rol == rolOpcion) FontWeight.Bold else FontWeight.Normal,
                            color = if (usuario.rol == rolOpcion) Marca else SoftInk
                        )
                    }
                }
            }

            TextButton(
                onClick = { onCambiarEstado(!usuario.activo) },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Text(
                    if (usuario.activo) "Bloquear acceso" else "Reactivar acceso",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (usuario.activo) Marca else SoftInk
                )
            }
        }
    }
}