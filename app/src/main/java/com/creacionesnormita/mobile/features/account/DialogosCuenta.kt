package com.creacionesnormita.mobile.features.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil

@Composable
internal fun EditarPerfilDialog(
    perfil: Perfil?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(perfil?.nombre.orEmpty()) }
    var celular by rememberSaveable { mutableStateOf(perfil?.celular.orEmpty()) }
    var otroContacto by rememberSaveable { mutableStateOf(perfil?.otro_contacto.orEmpty()) }

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
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nombre, celular, otroContacto) }
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
internal fun ConfirmDeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var textoConfirmacion by rememberSaveable { mutableStateOf("") }
    val esTextoValido = textoConfirmacion.trim().equals("ELIMINAR", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar cuenta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Esta acción es permanente e irreversible. Tu perfil y tus datos asociados serán eliminados de la plataforma.",
                    fontSize = 13.sp,
                    color = SoftInk
                )
                Text(
                    "Para confirmar, escribe ELIMINAR a continuación:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                OutlinedTextField(
                    value = textoConfirmacion,
                    onValueChange = { textoConfirmacion = it },
                    placeholder = { Text("ELIMINAR") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = esTextoValido
            ) {
                Text(
                    "Eliminar definitivamente",
                    color = if (esTextoValido) MaterialTheme.colorScheme.error else SoftInk,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SoftInk)
            }
        }
    )
}
