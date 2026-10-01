package com.creacionesnormita.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.creacionesnormita.mobile.core.model.Perfil
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

    val userEmail: String
        get() = SupabaseClient.client.auth.currentSessionOrNull()?.user?.email ?: "usuario@ejemplo.com"

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
        try {
            usuariosRegistrados = SupabaseClient.client
                .from("profiles")
                .select()
                .decodeList<Perfil>()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cargandoUsuarios = false
        }
    }

    suspend fun cambiarRolUsuario(usuarioId: String, nuevoRol: String): Boolean {
        try {
            val usuario = usuariosRegistrados.find { it.id == usuarioId } ?: return false
            val actualizado = usuario.copy(rol = nuevoRol)
            SupabaseClient.client.from("profiles").upsert(actualizado)
            cargarTodosLosUsuarios()
            if (perfilActual?.id == usuarioId) {
                perfilActual = actualizado
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
