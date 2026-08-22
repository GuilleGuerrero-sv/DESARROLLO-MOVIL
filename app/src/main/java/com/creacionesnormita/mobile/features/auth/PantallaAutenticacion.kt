package com.creacionesnormita.mobile.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.R
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.Sage
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.ui.components.ActionButton

@Composable
fun PantallaAutenticacion(onAuthenticated: () -> Unit) {
    var estaRegistrando by rememberSaveable { mutableStateOf(false) }
    var usuario by rememberSaveable { mutableStateOf("") }
    var claveLogin by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") }
    var celular by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var otroContacto by rememberSaveable { mutableStateOf("") }
    var claveRegistro by rememberSaveable { mutableStateOf("") }
    var confirmarClave by rememberSaveable { mutableStateOf("") }

    // Esta pantalla solo prepara el flujo visual; la autenticación real se agrega después.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Blush),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_normita),
                    contentDescription = "Creaciones Normita",
                    modifier = Modifier.fillMaxWidth(.58f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(26.dp))
            Text(
                text = if (estaRegistrando) "Crear cuenta" else "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Marca),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (estaRegistrando) "Completa tus datos para guardar citas, favoritos y cotizaciones." else "Entra con tu usuario, correo o WhatsApp.",
                color = SoftInk,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (estaRegistrando) {
                    AuthField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre completo",
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) }
                    )
                    AuthField(
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        label = "Fecha de nacimiento",
                        placeholder = "DD/MM/AAAA",
                        keyboardType = KeyboardType.Number,
                        icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) }
                    )
                    AuthField(
                        value = celular,
                        onValueChange = { celular = it },
                        label = "Número de celular",
                        keyboardType = KeyboardType.Phone,
                        icon = { Icon(Icons.Outlined.Phone, contentDescription = null) }
                    )
                    AuthField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = "Correo electrónico",
                        keyboardType = KeyboardType.Email,
                        icon = { Icon(Icons.Outlined.Email, contentDescription = null) }
                    )
                    AuthField(
                        value = otroContacto,
                        onValueChange = { otroContacto = it },
                        label = "Otro contacto",
                        placeholder = "Opcional",
                        icon = { Icon(Icons.Outlined.Phone, contentDescription = null) }
                    )
                    AuthField(
                        value = claveRegistro,
                        onValueChange = { claveRegistro = it },
                        label = "Contraseña",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) }
                    )
                    AuthField(
                        value = confirmarClave,
                        onValueChange = { confirmarClave = it },
                        label = "Comprobar contraseña",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) }
                    )
                } else {
                    AuthField(
                        value = usuario,
                        onValueChange = { usuario = it },
                        label = "Usuario, correo o WhatsApp",
                        keyboardType = KeyboardType.Email,
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) }
                    )
                    AuthField(
                        value = claveLogin,
                        onValueChange = { claveLogin = it },
                        label = "Contraseña",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            ActionButton(
                text = if (estaRegistrando) "Crear cuenta" else "Entrar",
                onClick = onAuthenticated,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (estaRegistrando) "¿Ya tienes cuenta?" else "¿Aún no tienes cuenta?",
                    color = SoftInk,
                    fontSize = 13.sp
                )
                TextButton(onClick = { estaRegistrando = !estaRegistrando }) {
                    Text(if (estaRegistrando) "Iniciar sesión" else "Crear cuenta", color = Marca, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Sage)
                    .padding(18.dp)
            ) {
                Text(
                    text = "Vestidos para quinceañeras, bodas y eventos especiales.",
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    icon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder, color = SoftInk)
            }
        },
        leadingIcon = icon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Ink,
            unfocusedBorderColor = Line,
            focusedLabelColor = Ink,
            cursorColor = Ink
        )
    )
}
