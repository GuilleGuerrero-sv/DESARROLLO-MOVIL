package com.creacionesnormita.mobile.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.core.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object SignUpSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            _authState.value = AuthState.Error("El correo es obligatorio")
            return
        }
        if (password.isEmpty()) {
            _authState.value = AuthState.Error("La contraseña es obligatoria")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = trimmedEmail
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = when {
                    e.message?.contains("Invalid login credentials", ignoreCase = true) == true -> "Correo o contraseña incorrectos"
                    e.message?.contains("network", ignoreCase = true) == true -> "Sin conexión a internet"
                    else -> "No se pudo iniciar sesión. Inténtalo de nuevo"
                }
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        nombre: String,
        fechaNacimiento: String,
        celular: String,
        otroContacto: String
    ) {
        val trimmedEmail = email.trim()
        
        // Mensajes amigables para campos vacíos
        val errorMsg = when {
            nombre.isBlank() -> "El nombre completo es obligatorio"
            fechaNacimiento.isBlank() -> "La fecha de nacimiento es obligatoria"
            celular.isBlank() -> "El número de celular es obligatorio"
            trimmedEmail.isBlank() -> "El correo electrónico es obligatorio"
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }

        if (errorMsg != null) {
            _authState.value = AuthState.Error(errorMsg)
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = trimmedEmail
                    this.password = password
                }
                
                val user = response
                
                if (user != null) {
                    val perfil = Perfil(
                        id = user.id,
                        nombre = nombre.trim(),
                        fecha_nacimiento = fechaNacimiento.trim(),
                        celular = celular.trim(),
                        otro_contacto = otroContacto.trim().ifBlank { null }
                    )
                    
                    // Primero guardamos el perfil
                    SupabaseClient.client.postgrest["profiles"].insert(perfil)
                    
                    // Cerramos sesión ANTES de avisar del éxito para evitar saltar a la pantalla principal
                    try {
                        SupabaseClient.client.auth.signOut()
                    } catch (e: Exception) {
                        // Ignorar errores de signout durante el registro
                    }
                    
                    _authState.value = AuthState.SignUpSuccess
                } else {
                    _authState.value = AuthState.Error("No pudimos crear tu cuenta")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val technicalError = when {
                    e.message?.contains("User already exists", ignoreCase = true) == true -> "Este correo ya está registrado"
                    e.message?.contains("network", ignoreCase = true) == true -> "Sin conexión a internet"
                    else -> "Error al registrarse. Revisa los datos"
                }
                _authState.value = AuthState.Error(technicalError)
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
