package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.mutableStateListOf
import com.creacionesnormita.mobile.core.model.ItemCotizacion
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.core.model.Talla

/**
 * Controller (MVC) singleton: al ser un `object`, el mismo estado se comparte
 * entre la pantalla de Detalle (donde se agregan productos) y la pestaña de
 * Cotizar (donde se ven/editan), sin necesidad de pasar nada por navegación.
 */
object CarritoController {
    val items = mutableStateListOf<ItemCotizacion>()

    private fun stockDisponible(producto: Producto, talla: Talla): Int =
        producto.stockPorTalla.firstOrNull { it.talla == talla }?.stock ?: 0

    /** Devuelve false si ya se alcanzó el stock máximo para esa talla y no se agregó nada más. */
    fun agregar(producto: Producto, talla: Talla): Boolean {
        val maxStock = stockDisponible(producto, talla)
        val index = items.indexOfFirst { it.producto.id == producto.id && it.talla == talla }

        if (index >= 0) {
            val actual = items[index]
            if (actual.cantidad >= maxStock) return false
            items[index] = actual.copy(cantidad = actual.cantidad + 1)
        } else {
            if (maxStock <= 0) return false
            items.add(ItemCotizacion(producto, talla))
        }
        return true
    }

    /** Devuelve false si ya se alcanzó el stock máximo para esa talla. */
    fun incrementar(item: ItemCotizacion): Boolean {
        val index = items.indexOf(item)
        if (index < 0) return false
        val maxStock = stockDisponible(item.producto, item.talla)
        if (item.cantidad >= maxStock) return false
        items[index] = item.copy(cantidad = item.cantidad + 1)
        return true
    }

    fun decrementar(item: ItemCotizacion) {
        val index = items.indexOf(item)
        if (index < 0) return
        if (item.cantidad <= 1) {
            items.removeAt(index)
        } else {
            items[index] = item.copy(cantidad = item.cantidad - 1)
        }
    }

    fun quitar(item: ItemCotizacion) {
        items.remove(item)
    }

    fun limpiar() {
        items.clear()
    }
}