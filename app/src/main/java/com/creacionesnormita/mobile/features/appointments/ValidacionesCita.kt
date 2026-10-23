package com.creacionesnormita.mobile.features.appointments

import android.util.Patterns
import androidx.compose.foundation.layout.size
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private val formatosFechaCita = listOf(
    DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
    DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT),
    DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT),
)
private val formatoSalidaFechaCita = DateTimeFormatter.ofPattern("dd/MM/uuuu")

private fun parseFechaCita(fecha: String): LocalDate? {
    val limpia = fecha.trim()
    return formatosFechaCita.firstNotNullOfOrNull { formato ->
        runCatching { LocalDate.parse(limpia, formato) }.getOrNull()
    }
}

internal fun fechaCitaAMillis(fecha: String): Long? {
    return parseFechaCita(fecha)
        ?.atStartOfDay(ZoneOffset.UTC)
        ?.toInstant()
        ?.toEpochMilli()
}

internal fun millisAFechaCita(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(formatoSalidaFechaCita)
}

private fun parseHoraCita(hora: String): LocalTime? {
    val partes = hora.trim().split(":")
    if (partes.size != 2) return null

    val horas = partes[0].toIntOrNull() ?: return null
    val minutos = partes[1].toIntOrNull() ?: return null

    return if (horas in 0..23 && minutos in 0..59) {
        LocalTime.of(horas, minutos)
    } else {
        null
    }
}

internal fun validarNombreCita(nombre: String): String? {
    val limpio = nombre.trim()
    return when {
        limpio.isBlank() -> "Ingresa el nombre de la persona que reserva."
        limpio.length < 3 -> "El nombre debe tener al menos 3 caracteres."
        !Regex("^[\\p{L} .'-]+$").matches(limpio) -> "El nombre solo debe contener letras y espacios."
        else -> null
    }
}

internal fun validarWhatsappCita(whatsapp: String): String? {
    val digitos = whatsapp.filter { it.isDigit() }
    return when {
        whatsapp.isBlank() -> "Ingresa un numero de WhatsApp."
        digitos.length < 8 -> "El WhatsApp debe tener al menos 8 digitos."
        digitos.length > 15 -> "El WhatsApp no debe pasar de 15 digitos."
        else -> null
    }
}

internal fun validarEmailCita(email: String): String? {
    val limpio = email.trim()
    return when {
        limpio.isBlank() -> null
        !Patterns.EMAIL_ADDRESS.matcher(limpio).matches() -> "Ingresa un correo valido."
        else -> null
    }
}

internal fun validarFechaCita(fecha: String): String? {
    val fechaCita = parseFechaCita(fecha)
    return when {
        fecha.isBlank() -> "Ingresa la fecha de la videollamada."
        fechaCita == null -> "Usa una fecha valida, por ejemplo 25/10/2026."
        fechaCita.isBefore(LocalDate.now()) -> "La fecha no puede estar en el pasado."
        else -> null
    }
}

internal fun validarHoraCita(hora: String, fecha: String): String? {
    val horaCita = parseHoraCita(hora)
    val fechaCita = parseFechaCita(fecha)
    return when {
        hora.isBlank() -> "Ingresa la hora de la videollamada."
        horaCita == null -> "Usa hora en formato 24 horas, por ejemplo 14:30."
        fechaCita == LocalDate.now() && horaCita.isBefore(LocalTime.now().plusMinutes(30)) -> {
            "Si la cita es hoy, elige una hora al menos 30 minutos despues."
        }
        else -> null
    }
}

internal fun validarMotivoCita(motivo: String): String? {
    val limpio = motivo.trim()
    return when {
        limpio.isBlank() -> "Indica que buscas para la cita."
        limpio.length < 4 -> "Describe un poco mas que necesitas."
        else -> null
    }
}

internal fun validarNotasCita(notas: String): String? {
    return if (notas.length > 280) "Las notas no deben pasar de 280 caracteres." else null
}
