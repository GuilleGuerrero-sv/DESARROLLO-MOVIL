package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.core.model.ProductoEscritura
import com.creacionesnormita.mobile.core.model.ProductoStockEscritura
import com.creacionesnormita.mobile.core.model.SoloDestacado
import com.creacionesnormita.mobile.core.model.SoloDisponible
import com.creacionesnormita.mobile.core.model.Talla
import com.creacionesnormita.mobile.core.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

/**
 * Controller (MVC): contiene la lógica para pedir datos de productos a Supabase
 * y expone el resultado como estado observable para que la View (Composable) reaccione.
 */
class ProductoController {

    // --- Vestido del día (Inicio) ---
    var vestidoDelDia by mutableStateOf<Producto?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // --- Lista completa pública: solo disponible = true (Colección) ---
    var productos by mutableStateOf<List<Producto>>(emptyList())
        private set
    var cargandoLista by mutableStateOf(false)
        private set
    var errorLista by mutableStateOf<String?>(null)
        private set

    // --- Producto individual (Detalle) ---
    var productoSeleccionado by mutableStateOf<Producto?>(null)
        private set
    var cargandoDetalle by mutableStateOf(false)
        private set
    var errorDetalle by mutableStateOf<String?>(null)
        private set

    // --- Lista completa para administración: incluye disponible = false ---
    var productosAdmin by mutableStateOf<List<Producto>>(emptyList())
        private set
    var cargandoAdmin by mutableStateOf(false)
        private set
    var errorAdmin by mutableStateOf<String?>(null)
        private set

    suspend fun cargarVestidoDelDia() {
        cargando = true
        error = null
        try {
            vestidoDelDia = SupabaseClient.client
                .from("producto")
                .select(Columns.raw("*, producto_stock(*)")) {
                    filter { eq("destacado", true) }
                    limit(1)
                }
                .decodeSingleOrNull<Producto>()
        } catch (e: Exception) {
            error = e.message
        } finally {
            cargando = false
        }
    }

    suspend fun cargarProductos() {
        cargandoLista = true
        errorLista = null
        try {
            productos = SupabaseClient.client
                .from("producto")
                .select(Columns.raw("*, producto_stock(*)")) {
                    filter { eq("disponible", true) }
                    order("creado_en", Order.DESCENDING)
                }
                .decodeList<Producto>()
        } catch (e: Exception) {
            errorLista = e.message
        } finally {
            cargandoLista = false
        }
    }

    suspend fun cargarProductoPorId(id: Int) {
        cargandoDetalle = true
        errorDetalle = null
        try {
            productoSeleccionado = SupabaseClient.client
                .from("producto")
                .select(Columns.raw("*, producto_stock(*)")) {
                    filter { eq("id", id) }
                }
                .decodeSingleOrNull<Producto>()
        } catch (e: Exception) {
            errorDetalle = e.message
        } finally {
            cargandoDetalle = false
        }
    }

    /** Trae TODOS los productos (incluyendo disponible = false) para el panel de administración. */
    suspend fun cargarProductosAdmin() {
        cargandoAdmin = true
        errorAdmin = null
        try {
            productosAdmin = SupabaseClient.client
                .from("producto")
                .select(Columns.raw("*, producto_stock(*)")) {
                    order("creado_en", Order.DESCENDING)
                }
                .decodeList<Producto>()
        } catch (e: Exception) {
            errorAdmin = e.message
        } finally {
            cargandoAdmin = false
        }
    }

    /** Crea un producto nuevo y sus filas de stock (una por cada talla en [stockPorTalla]). Devuelve el producto creado, o null si falló. */
    suspend fun crearProducto(datos: ProductoEscritura, stockPorTalla: Map<Talla, Int>): Producto? {
        return try {
            val insertado = SupabaseClient.client
                .from("producto")
                .insert(datos) { select() }
                .decodeSingle<Producto>()

            val filas = stockPorTalla.map { (talla, cantidad) ->
                ProductoStockEscritura(productoId = insertado.id, talla = talla, stock = cantidad)
            }
            if (filas.isNotEmpty()) {
                SupabaseClient.client.from("producto_stock").insert(filas)
            }
            insertado
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            null
        }
    }

    /** Actualiza los datos generales del producto (no toca destacado ni stock). */
    suspend fun actualizarProducto(id: Int, datos: ProductoEscritura): Boolean {
        return try {
            // select() devuelve la fila actualizada; si viene vacía, Supabase no aplicó el cambio (RLS o id inexistente).
            val filas = SupabaseClient.client
                .from("producto")
                .update(datos) {
                    select()
                    filter { eq("id", id) }
                }
                .decodeList<Producto>()
            if (filas.isEmpty()) {
                errorAdmin = "Supabase no aplicó el cambio. Revisa las políticas (RLS) de la tabla producto."
                return false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }

    /** Crea o actualiza el stock de UNA talla puntual (según el constraint único producto_id+talla). */
    suspend fun actualizarStockTalla(productoId: Int, talla: Talla, nuevoStock: Int): Boolean {
        return try {
            SupabaseClient.client
                .from("producto_stock")
                .upsert(
                    ProductoStockEscritura(productoId = productoId, talla = talla, stock = nuevoStock)
                ) {
                    onConflict = "producto_id,talla"
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }

    suspend fun actualizarDisponibilidad(id: Int, disponible: Boolean): Boolean {
        return try {
            SupabaseClient.client
                .from("producto")
                .update(SoloDisponible(disponible)) { filter { eq("id", id) } }
            productosAdmin = productosAdmin.map { if (it.id == id) it.copy(disponible = disponible) else it }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }

    /** Marca [id] como vestido del día y le quita esa marca a cualquier otro que la tuviera. */
    suspend fun marcarComoVestidoDelDia(id: Int): Boolean {
        return try {
            SupabaseClient.client
                .from("producto")
                .update(SoloDestacado(false)) { filter { eq("destacado", true) } }
            SupabaseClient.client
                .from("producto")
                .update(SoloDestacado(true)) { filter { eq("id", id) } }
            productosAdmin = productosAdmin.map { it.copy(destacado = it.id == id) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }

    suspend fun quitarVestidoDelDia(id: Int): Boolean {
        return try {
            SupabaseClient.client
                .from("producto")
                .update(SoloDestacado(false)) { filter { eq("id", id) } }
            productosAdmin = productosAdmin.map { if (it.id == id) it.copy(destacado = false) else it }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }

    suspend fun eliminarProducto(id: Int): Boolean {
        return try {
            SupabaseClient.client
                .from("producto")
                .delete { filter { eq("id", id) } }
            productosAdmin = productosAdmin.filterNot { it.id == id }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorAdmin = e.message
            false
        }
    }
}