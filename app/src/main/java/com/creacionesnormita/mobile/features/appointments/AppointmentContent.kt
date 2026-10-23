package com.creacionesnormita.mobile.features.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.controller.PerfilController
import com.creacionesnormita.mobile.core.abrirWhatsApp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.features.account.PerfilResumenCard
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.CampoFormulario

// Trabajo Guillermo: pantalla de citas por videollamada usando los datos del perfil en sesion.
@Composable
internal fun AppointmentContent(perfilController: PerfilController) {
    val context = LocalContext.current
    val perfil = perfilController.perfilActual

    var nombre by rememberSaveable { mutableStateOf("") }
    var whatsapp by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }
    var motivo by rememberSaveable { mutableStateOf("") }
    var notas by rememberSaveable { mutableStateOf("") }
    var yaPrecargado by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(perfil) {
        if (perfil != null && !yaPrecargado) {
            nombre = perfil.nombre
            whatsapp = perfil.celular
            email = perfilController.userEmail
            yaPrecargado = true
        }
    }

    // Trabajo Guillermo: validaciones del formulario antes de enviar la solicitud por WhatsApp.
    val nombreError = validarNombreCita(nombre)
    val whatsappError = validarWhatsappCita(whatsapp)
    val emailError = validarEmailCita(email)
    val fechaError = validarFechaCita(fecha)
    val horaError = validarHoraCita(hora, fecha)
    val motivoError = validarMotivoCita(motivo)
    val notasError = validarNotasCita(notas)
    val erroresCita = listOfNotNull(
        nombreError,
        whatsappError,
        emailError,
        fechaError,
        horaError,
        motivoError,
        notasError
    )
    val puedeReservar = erroresCita.isEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            PerfilResumenCard(
                perfil = perfil,
                email = perfilController.userEmail
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Ink, modifier = Modifier.size(30.dp))
                Text("Cita por videollamada", color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Agenda una asesoría y enviaremos la solicitud por WhatsApp con tus datos de cuenta.",
                    color = SoftInk,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        item {
            CampoFormulario(
                label = "Nombre",
                value = nombre,
                onValueChange = { nombre = it },
                errorText = nombreError.takeIf { nombre.isNotBlank() }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoFormulario(
                    label = "WhatsApp",
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    modifier = Modifier.weight(1f),
                    errorText = whatsappError.takeIf { whatsapp.isNotBlank() }
                )
                CampoFormulario(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.weight(1f),
                    errorText = emailError
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppointmentDateField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    modifier = Modifier.weight(1f),
                    errorText = fechaError.takeIf { fecha.isNotBlank() }
                )
                CampoFormulario(
                    label = "Hora",
                    value = hora,
                    onValueChange = { hora = it },
                    modifier = Modifier.weight(1f),
                    errorText = horaError.takeIf { hora.isNotBlank() }
                )
            }
        }
        item {
            CampoFormulario(
                label = "Qué buscas? (quinceañera, boda...)",
                value = motivo,
                onValueChange = { motivo = it },
                errorText = motivoError.takeIf { motivo.isNotBlank() }
            )
        }
        item {
            CampoFormulario(
                label = "Notas para la asesora",
                value = notas,
                onValueChange = { notas = it },
                singleLine = false,
                minLines = 3,
                errorText = notasError
            )
        }
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                ActionButton(
                    text = "Reservar videollamada",
                    enabled = puedeReservar,
                    onClick = {
                        val mensaje = construirMensajeCita(
                            nombre = nombre,
                            whatsapp = whatsapp,
                            email = email,
                            fecha = fecha,
                            hora = hora,
                            motivo = motivo,
                            notas = notas
                        )
                        abrirWhatsApp(context, mensaje)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!puedeReservar) {
                    Text(
                        erroresCita.firstOrNull() ?: "Completa los datos requeridos para reservar",
                        fontSize = 11.sp,
                        color = SoftInk,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

// Trabajo Guillermo: arma el mensaje que se envia por WhatsApp al reservar la videollamada.
private fun construirMensajeCita(
    nombre: String,
    whatsapp: String,
    email: String,
    fecha: String,
    hora: String,
    motivo: String,
    notas: String,
): String = buildString {
    append("¡Hola! Quiero reservar una cita por videollamada.\n\n")
    append("Nombre: $nombre\n")
    append("WhatsApp: $whatsapp\n")
    if (email.isNotBlank()) append("Email: $email\n")
    append("Fecha: $fecha\n")
    append("Hora: $hora\n")
    if (motivo.isNotBlank()) append("Busco: $motivo\n")
    if (notas.isNotBlank()) append("Notas: $notas\n")
}
