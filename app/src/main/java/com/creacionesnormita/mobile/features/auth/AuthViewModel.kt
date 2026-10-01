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
    object PasswordUpdateSuccess : AuthState()
    data class ResetPasswordSuccess(val emailSentTo: String) : AuthState()
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

        if (!SupabaseClient.isConfigured()) {
            _authState.value = AuthState.Error(
                "No se ha configurado la conexión a Supabase. Agrega SUPABASE_URL y SUPABASE_KEY en local.properties"
            )
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
                _authState.value = AuthState.Error(parseErrorMessage(e, isLogin = true))
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        nombre: String,
        fechaNacimiento: String,
        celular: String,
        otroContacto: String,
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

        if (!SupabaseClient.isConfigured()) {
            _authState.value = AuthState.Error(
                "No se ha configurado la conexión a Supabase. Agrega SUPABASE_URL y SUPABASE_KEY en local.properties"
            )
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = trimmedEmail
                    this.password = password
                }
                
                if (user != null) {
                    val perfil = Perfil(
                        id = user.id,
                        nombre = nombre.trim(),
                        fecha_nacimiento = fechaNacimiento.trim(),
                        celular = celular.trim(),
                        otro_contacto = otroContacto.trim().ifBlank { null }
                    )
                    
                    // Intentar guardar el perfil en la base de datos
                    try {
                        SupabaseClient.client.postgrest["profiles"].insert(perfil)
                    } catch (pe: Exception) {
                        pe.printStackTrace()
                        // Si guardar el perfil falla (por ejemplo si RLS o confirmación de email lo impide),
                        // no interrumpimos el registro de la cuenta del usuario en Auth.
                    }
                    
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
                _authState.value = AuthState.Error(parseErrorMessage(e, isLogin = false))
            }
        }
    }
    
    private fun parseErrorMessage(e: Exception, isLogin: Boolean): String {
        val msg = e.message.orEmpty()
        val cause = e.cause?.message.orEmpty()
        val combined = "$msg $cause".lowercase()

        return when {
            // Límite de peticiones / Supabase Rate limit (Too Many Requests / 429)
            combined.contains("rate limit") ||
            combined.contains("too many requests") ||
            combined.contains("over_email_send_rate_limit") ||
            combined.contains("over_request_rate_limit") ||
            combined.contains("429") -> {
                "Se han realizado demasiados intentos en poco tiempo. Supabase requiere esperar unos minutos antes de intentar de nuevo."
            }

            // Credenciales inválidas
            combined.contains("invalid login credentials") ||
            combined.contains("invalid_credentials") -> {
                "Correo o contraseña incorrectos."
            }

            // Usuario ya existente
            combined.contains("user already registered") ||
            combined.contains("user_already_exists") ||
            combined.contains("already registered") ||
            combined.contains("already exists") -> {
                "Este correo electrónico ya está registrado. Por favor inicia sesión."
            }

            // Correo no confirmado
            combined.contains("email not confirmed") ||
            combined.contains("email_not_confirmed") -> {
                "Debes confirmar tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada o spam."
            }

            // Contraseña débil
            combined.contains("password should be at least") ||
            combined.contains("weak_password") -> {
                "La contraseña debe tener al menos 6 caracteres."
            }

            // Error de conexión o host
            combined.contains("network") ||
            combined.contains("unable to resolve host") ||
            combined.contains("failed to connect") ||
            combined.contains("unknownhostexception") -> {
                "Sin conexión a internet o la URL de Supabase es inaccesible."
            }

            // Error de autorización / token expirado
            combined.contains("authorization") ||
            combined.contains("unauthorized") ||
            combined.contains("401") -> {
                "Sesión de recuperación expirada o no autorizada. Por favor, solicita de nuevo el correo de recuperación."
            }

            // Si hay un mensaje explicativo retornado por Supabase, mostrarlo directamente
            msg.isNotBlank() && !msg.equals("null", ignoreCase = true) -> {
                if (isLogin) "No se pudo iniciar sesión: $msg" else "Error al registrarse: $msg"
            }

            else -> {
                if (isLogin) "No se pudo iniciar sesión. Por favor, inténtalo de nuevo más tarde."
                else "Error al registrarse. Por favor, inténtalo de nuevo más tarde."
            }
        }
    }

    fun resetPassword(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            _authState.value = AuthState.Error("Ingresa tu correo para restablecer la contraseña")
            return
        }

        if (!SupabaseClient.isConfigured()) {
            _authState.value = AuthState.Error("No se ha configurado la conexión a Supabase")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.resetPasswordForEmail(
                    email = trimmedEmail,
                    redirectUrl = "creacionesnormita://reset-password"
                )
                _authState.value = AuthState.ResetPasswordSuccess(trimmedEmail)
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(parseErrorMessage(e, isLogin = true))
            }
        }
    }

    fun updatePassword(nuevaPassword: String) {
        if (nuevaPassword.isBlank() || nuevaPassword.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (!SupabaseClient.isConfigured()) {
            _authState.value = AuthState.Error("No se ha configurado la conexión a Supabase")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val session = SupabaseClient.client.auth.currentSessionOrNull()
                if (session == null) {
                    _authState.value = AuthState.Error(
                        "No hay una sesión activa de recuperación. Asegúrate de abrir el enlace desde tu correo electrónico."
                    )
                    return@launch
                }

                SupabaseClient.client.auth.updateUser {
                    this.password = nuevaPassword
                }
                _authState.value = AuthState.PasswordUpdateSuccess
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(parseErrorMessage(e, isLogin = true))
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
