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

    fun agregar(producto: Producto, talla: Talla) {
        val index = items.indexOfFirst { it.producto.id == producto.id && it.talla == talla }
        if (index >= 0) {
            items[index] = items[index].copy(cantidad = items[index].cantidad + 1)
        } else {
            items.add(ItemCotizacion(producto, talla))
        }
    }

    fun incrementar(item: ItemCotizacion) {
        val index = items.indexOf(item)
        if (index >= 0) items[index] = item.copy(cantidad = item.cantidad + 1)
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