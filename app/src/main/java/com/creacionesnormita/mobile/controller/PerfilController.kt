package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.core.model.SoloActivo
import com.creacionesnormita.mobile.core.model.SoloRol
import com.creacionesnormita.mobile.core.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class PerfilController {
    var perfilActual by mutableStateOf<Perfil?>(null)
        private set
    var cargandoPerfil by mutableStateOf(false)
        private set
    var errorPerfil by mutableStateOf<String?>(null)
        private set

    var usuariosRegistrados by mutableStateOf<List<Perfil>>(emptyList())
        private set
    var cargandoUsuarios by mutableStateOf(false)
        private set
    var errorUsuarios by mutableStateOf<String?>(null)
        private set

    val userEmail: String
        get() = SupabaseClient.client.auth.currentSessionOrNull()?.user?.email ?: "usuario@ejemplo.com"

    val currentUserId: String?
        get() = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id

    /** Se vuelve true si el perfil cargado está bloqueado; AppRoot debe cerrar la sesión cuando esto pase. */
    var cuentaBloqueada by mutableStateOf(false)
        private set

    suspend fun cargarPerfilActual() {
        cargandoPerfil = true
        errorPerfil = null
        try {
            val sessionUser = SupabaseClient.client.auth.currentSessionOrNull()?.user
            if (sessionUser != null) {
                val perfil = SupabaseClient.client
                    .from("profiles")
                    .select {
                        filter { eq("id", sessionUser.id) }
                        limit(1)
                    }
                    .decodeSingleOrNull<Perfil>()

                perfilActual = perfil ?: Perfil(
                    id = sessionUser.id,
                    nombre = sessionUser.userMetadata?.get("full_name")?.toString() ?: "Usuario",
                    fecha_nacimiento = "",
                    celular = "",
                    rol = Perfil.ROL_CLIENTE
                )

                if (perfil != null && !perfil.activo) {
                    cuentaBloqueada = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorPerfil = e.message
        } finally {
            cargandoPerfil = false
        }
    }

    suspend fun actualizarPerfil(
        nombre: String,
        celular: String,
        otroContacto: String?,
        fotoUrl: String?
    ): Boolean {
        val actual = perfilActual ?: return false
        cargandoPerfil = true
        try {
            val nuevoPerfil = actual.copy(
                nombre = nombre.trim(),
                celular = celular.trim(),
                otro_contacto = otroContacto?.trim()?.ifBlank { null },
                foto_url = fotoUrl?.trim()?.ifBlank { null }
            )

            SupabaseClient.client.from("profiles").upsert(nuevoPerfil)
            perfilActual = nuevoPerfil
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            errorPerfil = e.message
            return false
        } finally {
            cargandoPerfil = false
        }
    }

    suspend fun cargarTodosLosUsuarios() {
        cargandoUsuarios = true
        errorUsuarios = null
        try {
            usuariosRegistrados = SupabaseClient.client
                .from("profiles")
                .select()
                .decodeList<Perfil>()
        } catch (e: Exception) {
            e.printStackTrace()
            errorUsuarios = e.message
        } finally {
            cargandoUsuarios = false
        }
    }

    /**
     * Cambia SOLO la columna `rol` (con update, no upsert). Pide de vuelta la fila actualizada:
     * si Supabase la bloquea por RLS devuelve 0 filas sin lanzar error, y así lo detectamos.
     */
    suspend fun cambiarRolUsuario(usuarioId: String, nuevoRol: String): Boolean {
        errorUsuarios = null
        try {
            val filas = SupabaseClient.client
                .from("profiles")
                .update(SoloRol(nuevoRol)) {
                    select()
                    filter { eq("id", usuarioId) }
                }
                .decodeList<Perfil>()

            if (filas.isEmpty()) {
                errorUsuarios = "Supabase no aplicó el cambio. Revisa las políticas (RLS) de la tabla profiles."
                return false
            }
            usuariosRegistrados = usuariosRegistrados.map {
                if (it.id == usuarioId) it.copy(rol = nuevoRol) else it
            }
            if (perfilActual?.id == usuarioId) {
                perfilActual = perfilActual?.copy(rol = nuevoRol)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            errorUsuarios = e.message
            return false
        }
    }

    /**
     * No borra la cuenta de Supabase Auth (eso requeriría la service_role key,
     * que nunca debe estar en la app). En su lugar, bloquea el acceso: la próxima
     * vez que esa persona intente entrar, [cargarPerfilActual] detecta activo=false
     * y AppRoot la saca de sesión automáticamente.
     */
    suspend fun cambiarEstadoUsuario(usuarioId: String, activo: Boolean): Boolean {
        errorUsuarios = null
        try {
            val filas = SupabaseClient.client
                .from("profiles")
                .update(SoloActivo(activo)) {
                    select()
                    filter { eq("id", usuarioId) }
                }
                .decodeList<Perfil>()

            if (filas.isEmpty()) {
                errorUsuarios = "Supabase no aplicó el cambio. Revisa las políticas (RLS) de la tabla profiles."
                return false
            }
            usuariosRegistrados = usuariosRegistrados.map {
                if (it.id == usuarioId) it.copy(activo = activo) else it
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            errorUsuarios = e.message
            return false
        }
    }
}