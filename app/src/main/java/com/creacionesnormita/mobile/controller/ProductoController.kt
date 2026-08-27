package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Producto
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

    // --- Lista completa (Colección) ---
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
}