package com.creacionesnormita.mobile.features.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creacionesnormita.mobile.MainActivity
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
fun PantallaAutenticacion(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
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

    var nuevaPassword by rememberSaveable { mutableStateOf("") }
    var confirmarNuevaPassword by rememberSaveable { mutableStateOf("") }

    var mostrarDialogoOlvidado by rememberSaveable { mutableStateOf(false) }

    val authState by viewModel.authState
    val context = LocalContext.current
    val estaEnFlujoRecuperacion = MainActivity.esFlujoRecuperacion

    // Validaciones visuales
    val emailValido = Patterns.EMAIL_ADDRESS.matcher(if (estaRegistrando) correo else usuario).matches()
    val clavesCoinciden = claveRegistro == confirmarClave
    val nuevasClavesCoinciden = nuevaPassword == confirmarNuevaPassword

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onAuthenticated()
                viewModel.resetState()
            }
            is AuthState.SignUpSuccess -> {
                Toast.makeText(context, "¡Cuenta creada con éxito! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                estaRegistrando = false
                viewModel.resetState()
            }
            is AuthState.ResetPasswordSuccess -> {
                val emailEnviado = (authState as AuthState.ResetPasswordSuccess).emailSentTo
                Toast.makeText(context, "Enlace enviado a $emailEnviado. Al abrir el correo se activará el cambio de clave.", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is AuthState.PasswordUpdateSuccess -> {
                Toast.makeText(context, "¡Contraseña actualizada con éxito! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                MainActivity.esFlujoRecuperacion = false
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    if (mostrarDialogoOlvidado) {
        var correoRecuperacion by rememberSaveable { mutableStateOf(usuario) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoOlvidado = false },
            title = { Text("Recuperar contraseña", fontWeight = FontWeight.Bold, color = Ink) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Ingresa tu correo registrado y te enviaremos el enlace para restablecer tu contraseña.",
                        fontSize = 13.sp,
                        color = SoftInk
                    )
                    OutlinedTextField(
                        value = correoRecuperacion,
                        onValueChange = { correoRecuperacion = it },
                        label = { Text("CORREO ELECTRÓNICO", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    usuario = correoRecuperacion
                    viewModel.resetPassword(correoRecuperacion)
                    mostrarDialogoOlvidado = false
                }) {
                    Text("Enviar enlace", color = Marca, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoOlvidado = false }) {
                    Text("Cancelar", color = SoftInk)
                }
            }
        )
    }

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
                text = when {
                    estaEnFlujoRecuperacion -> "Restablecer contraseña"
                    estaRegistrando -> "Crear cuenta"
                    else -> "Iniciar sesión"
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Marca),
                textAlign = TextAlign.Center
            )
            Text(
                text = when {
                    estaEnFlujoRecuperacion -> "Define tu nueva contraseña para completar la recuperación de tu cuenta."
                    estaRegistrando -> "Completa tus datos para guardar citas, favoritos y cotizaciones."
                    else -> "Entra con tu correo electrónico y contraseña."
                },
                color = SoftInk,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (estaEnFlujoRecuperacion) {
                    AuthField(
                        value = nuevaPassword,
                        onValueChange = { nuevaPassword = it },
                        label = "Nueva contraseña*",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = confirmarNuevaPassword,
                        onValueChange = { confirmarNuevaPassword = it },
                        label = "Confirmar nueva contraseña*",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        isError = confirmarNuevaPassword.isNotEmpty() && !nuevasClavesCoinciden,
                        imeAction = ImeAction.Done
                    )
                } else if (estaRegistrando) {
                    AuthField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre completo*",
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = fechaNacimiento,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(8)
                            val formatted = buildString {
                                for (i in clean.indices) {
                                    append(clean[i])
                                    if ((i == 1 || i == 3) && i != clean.lastIndex) append("/")
                                }
                            }
                            fechaNacimiento = formatted
                        },
                        label = "Fecha de nacimiento*",
                        placeholder = "DD/MM/AAAA",
                        keyboardType = KeyboardType.Number,
                        icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = celular,
                        onValueChange = { celular = it },
                        label = "Número de celular*",
                        keyboardType = KeyboardType.Phone,
                        icon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = "Correo electrónico*",
                        keyboardType = KeyboardType.Email,
                        icon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        isError = correo.isNotEmpty() && !emailValido,
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = otroContacto,
                        onValueChange = { otroContacto = it },
                        label = "Otro contacto",
                        placeholder = "Opcional",
                        icon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = claveRegistro,
                        onValueChange = { claveRegistro = it },
                        label = "Contraseña*",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = confirmarClave,
                        onValueChange = { confirmarClave = it },
                        label = "Comprobar contraseña*",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        isError = confirmarClave.isNotEmpty() && !clavesCoinciden,
                        imeAction = ImeAction.Done
                    )
                } else {
                    AuthField(
                        value = usuario,
                        onValueChange = { usuario = it },
                        label = "Correo electrónico*",
                        keyboardType = KeyboardType.Email,
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        isError = usuario.isNotEmpty() && !emailValido,
                        imeAction = ImeAction.Next
                    )
                    AuthField(
                        value = claveLogin,
                        onValueChange = { claveLogin = it },
                        label = "Contraseña*",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        icon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        imeAction = ImeAction.Done
                    )

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(
                            onClick = { mostrarDialogoOlvidado = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("¿Olvidaste tu contraseña?", color = Marca, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Marca, modifier = Modifier.size(32.dp))
            } else {
                ActionButton(
                    text = when {
                        estaEnFlujoRecuperacion -> "Actualizar contraseña"
                        estaRegistrando -> "Crear cuenta"
                        else -> "Entrar"
                    },
                    onClick = {
                        when {
                            estaEnFlujoRecuperacion -> {
                                if (nuevaPassword == confirmarNuevaPassword) {
                                    viewModel.updatePassword(nuevaPassword)
                                } else {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                }
                            }
                            estaRegistrando -> {
                                if (claveRegistro == confirmarClave) {
                                    viewModel.signUp(correo, claveRegistro, nombre, fechaNacimiento, celular, otroContacto)
                                } else {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> {
                                viewModel.login(usuario, claveLogin)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        estaEnFlujoRecuperacion -> "¿Cancelar recuperación?"
                        estaRegistrando -> "¿Ya tienes cuenta?"
                        else -> "¿Aún no tienes cuenta?"
                    },
                    color = SoftInk,
                    fontSize = 13.sp
                )
                TextButton(onClick = {
                    if (estaEnFlujoRecuperacion) {
                        MainActivity.esFlujoRecuperacion = false
                    } else {
                        estaRegistrando = !estaRegistrando
                    }
                    viewModel.resetState()
                }) {
                    Text(
                        if (estaEnFlujoRecuperacion || estaRegistrando) "Iniciar sesión" else "Crear cuenta",
                        color = Marca,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!estaEnFlujoRecuperacion) {
                // --- Sección de Login Social ---
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
                    Text(
                        " O entra con ",
                        color = SoftInk,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Line)
                }
                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SocialButton(
                        text = "Google",
                        icon = Icons.Outlined.AccountCircle,
                        onClick = {
                            viewModel.loginWithGoogle()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        text = "Facebook",
                        icon = Icons.Outlined.Share,
                        onClick = {
                            viewModel.loginWithFacebook()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        text = "X",
                        icon = Icons.Outlined.AlternateEmail,
                        onClick = {
                            viewModel.loginWithTwitter()
                        },
                        modifier = Modifier.weight(1f)
                    )
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
private fun SocialButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Line),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Ink
        ),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Marca)
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ink)
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
    isError: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    icon: @Composable () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = SoftInk
                    )
                }
            }
        } else null,
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Ink,
            unfocusedBorderColor = Line,
            focusedLabelColor = Ink,
            cursorColor = Ink,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}
