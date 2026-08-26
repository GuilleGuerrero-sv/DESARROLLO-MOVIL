package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Producto
import com.creacionesnormita.mobile.data.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

/**
 * Controller (MVC): contiene la lógica para pedir datos de productos a Supabase
 * y expone el resultado como estado observable para que la View (Composable) reaccione.
 */
class ProductoController {

    var vestidoDelDia by mutableStateOf<Producto?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
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
}