package com.creacionesnormita.mobile.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.creacionesnormita.mobile.core.Constantes
import java.net.URLEncoder

/** Abre el chat de WhatsApp del negocio con un mensaje inicial. */
fun abrirWhatsApp(
    context: Context,
    mensaje: String = "¡Hola! Quisiera más información sobre sus vestidos."
) {
    try {
        val uri = Uri.parse(
            "https://wa.me/${Constantes.WHATSAPP_NUMERO}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}
