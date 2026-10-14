package com.creacionesnormita.mobile.controller

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Producto

object FavoritosController {
    private const val PREFS_NAME = "favoritos_normita"
    private const val KEY_PREFIX = "productos_favoritos_"

    private var llaveActual: String? = null

    // Trabajo Guillermo: mantiene los vestidos favoritos separados por usuario en sesion.
    var idsFavoritos by mutableStateOf<List<Int>>(emptyList())
        private set

    fun cargar(context: Context, userId: String?) {
        val llave = llavePara(userId)
        if (llaveActual == llave) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        idsFavoritos = prefs.getString(llave, null)
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            .orEmpty()
        llaveActual = llave
    }

    fun estaEnFavoritos(productoId: Int): Boolean = idsFavoritos.contains(productoId)

    fun alternar(context: Context, userId: String?, productoId: Int): Boolean {
        cargar(context, userId)
        idsFavoritos = if (estaEnFavoritos(productoId)) {
            idsFavoritos.filterNot { it == productoId }
        } else {
            listOf(productoId) + idsFavoritos.filterNot { it == productoId }
        }
        guardar(context)
        return estaEnFavoritos(productoId)
    }

    fun productosFavoritos(productos: List<Producto>, limite: Int? = null): List<Producto> {
        val porId = productos.associateBy { it.id }
        val resultado = idsFavoritos.mapNotNull { porId[it] }
        return limite?.let { resultado.take(it) } ?: resultado
    }

    private fun guardar(context: Context) {
        val llave = llaveActual ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(llave, idsFavoritos.joinToString(","))
            .apply()
    }

    private fun llavePara(userId: String?): String = KEY_PREFIX + (userId ?: "invitado")
}
